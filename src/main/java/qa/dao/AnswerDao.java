package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.cache.entity.like.LikesUtil;
import qa.cache.operation.impl.AnswerToLikeSetOperation;
import qa.cache.operation.impl.UserAnswerLikeSetOperation;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.dao.query.AnswerQueryCreator;
import qa.dao.query.convertor.AnswerQueryResultConvertor;
import qa.domain.Answer;
import qa.domain.setters.PropertySetterFactory;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryConfigurer;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnswerDao extends DaoImpl<Answer> implements Likeable<Long> {

    private final SessionFactory sessionFactory;
    private final JedisResourceCenter jedisResourceCenter;

    private static final AnswerToLikeSetOperation answerToLikeOperation;
    private static final UserAnswerLikeSetOperation userToAnswerLikeOperation;

    static {
        answerToLikeOperation = new AnswerToLikeSetOperation();
        userToAnswerLikeOperation = new UserAnswerLikeSetOperation();
    }

    @Autowired
    public AnswerDao(PropertySetterFactory propertySetterFactory,
                     SessionFactory sessionFactory,
                     JedisResourceCenter jedisResourceCenter) {
        super(HibernateSessionFactoryConfigurer.getSessionFactory(), new Answer(), propertySetterFactory.getSetter(new Answer()));
        this.sessionFactory = sessionFactory;
        this.jedisResourceCenter = jedisResourceCenter;
    }

    @Override
    public Long create(Answer e) {
        final Long id = (Long) super.create(e);
        this.createLike(id);
        return id;
    }

    public void delete(long answerId) {
        final Where where = new Where("id", answerId, WhereOperator.EQUALS);
        super.delete(where);
        this.deleteLikes(answerId);
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

            final boolean status = userToAnswerLikeOperation.add(userId, id, jedis);
            if (status) answerToLikeOperation.increment(id, jedis);
        }
    }

    private void createLike(long answerId) {
        try(JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();

            LikesUtil.createLike(answerId, answerToLikeOperation, jedis);
        }
    }

    private void deleteLikes(long questionId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();

            answerToLikeOperation.delete(questionId, jedis);
            userToAnswerLikeOperation.deleteEntity(questionId, jedis);
        }
    }

    private void setLikes(List<Answer> answers, long userId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();

            LikesUtil.setLikesAndLiked(answers, userId, answerToLikeOperation, userToAnswerLikeOperation, jedis);
        }
    }
}
