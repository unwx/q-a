package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.CacheRemoveInstructions;
import qa.cache.CacheRemover;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.cache.entity.like.LikesUtil;
import qa.cache.entity.like.provider.AnswerCacheProvider;
import qa.cache.operation.impl.AnswerToLikeSetOperation;
import qa.cache.operation.impl.UserAnswerLikeSetOperation;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.dao.query.AnswerQueryCreator;
import qa.dao.query.convertor.AnswerQueryResultConvertor;
import qa.domain.Answer;
import qa.domain.DomainName;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.answer.AnswerFullStringIdsDto;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryConfigurer;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Component
public class AnswerDao extends DaoImpl<Answer> implements Likeable<Long> {

    private final SessionFactory sessionFactory;
    private final JedisResourceCenter jedisResourceCenter;
    private final CacheRemover cacheRemover;
    private final AnswerCacheProvider cacheProvider;

    private static final AnswerToLikeSetOperation answerToLikeOperation;
    private static final UserAnswerLikeSetOperation userToAnswerLikeOperation;

    static {
        answerToLikeOperation = new AnswerToLikeSetOperation();
        userToAnswerLikeOperation = new UserAnswerLikeSetOperation();
    }

    @Autowired
    public AnswerDao(PropertySetterFactory propertySetterFactory,
                     SessionFactory sessionFactory,
                     JedisResourceCenter jedisResourceCenter,
                     CacheRemover cacheRemover,
                     AnswerCacheProvider cacheProvider) {
        super(HibernateSessionFactoryConfigurer.getSessionFactory(), new Answer(), propertySetterFactory.getSetter(new Answer()));
        this.sessionFactory = sessionFactory;
        this.jedisResourceCenter = jedisResourceCenter;
        this.cacheRemover = cacheRemover;
        this.cacheProvider = cacheProvider;
    }

    @Override
    public Long create(Answer e) {
        final Long id = (Long) super.create(e);
        this.createLike(id);
        return id;
    }

    public void delete(long answerId) {
        final Where where = new Where("id", answerId, WhereOperator.EQUALS);
        final AnswerFullStringIdsDto answerFullStringIdsDto;
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            answerFullStringIdsDto = AnswerQueryCreator
                    .answerFullIdsQuery(session, answerId)
                    .uniqueResult();

            if (answerFullStringIdsDto == null) {
                transaction.rollback();
                return;
            }

            transaction.commit();
        }
        super.delete(where);
        this.deleteLikes(answerFullStringIdsDto, answerId);
    }

    public boolean isExist(Long id) {
        return super.isExist(new Where("id", id, WhereOperator.EQUALS), "Answer");
    }

    @Nullable
    public List<Answer> getAnswers(long questionId, long userId, int page) {

        /*
         *  if question not exist: answers.size() = 0; (NullResultException will not be thrown) - return null
         *  if answers not exist: NullResultException - return empty list.
         *  if exist: return result.
         */

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Answer> answers = new ArrayList<>();

            try {
                answers = AnswerQueryResultConvertor
                        .dtoToAnswerList(AnswerQueryCreator
                                .answersWithCommentsQuery(session, questionId, page)
                                .list()
                        );
            } catch (NullResultException ex) {
                transaction.rollback();
                return answers;
            }

            if (answers.isEmpty()) {
                transaction.rollback();
                return null;
            }

            transaction.commit();
            setLikes(answers, userId);
            return answers;
        }
    }

    @Override
    public void like(long userId, Long id) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            final String userIdStr = String.valueOf(userId);
            final String idStr = String.valueOf(id);

            LikesUtil.like(userIdStr, idStr, userToAnswerLikeOperation, answerToLikeOperation, jedis);
        }
    }

    private void createLike(long answerId) {
        try(JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();

            LikesUtil.createLike(String.valueOf(answerId), answerToLikeOperation, jedis);
        }
    }

    private void deleteLikes(AnswerFullStringIdsDto dto, long answerId) {
        final CacheRemoveInstructions instructions = new CacheRemoveInstructions();
        final Stack<String> answerIdStr = new Stack<>();
        answerIdStr.push(String.valueOf(answerId));

        instructions.addInstruction(DomainName.ANSWER, answerIdStr);
        instructions.addInstruction(DomainName.COMMENT_ANSWER, dto.getCommentAnswerIds());

        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            cacheRemover.remove(instructions, jedis);
        }
    }

    private void setLikes(List<Answer> answers, long userId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();

            this.cacheProvider.provide(answers, userId, jedis);
        }
    }
}
