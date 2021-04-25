package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.cache.entity.like.LikesUtil;
import qa.cache.operation.impl.QuestionToLikeSetOperation;
import qa.cache.operation.impl.UserQuestionLikeSetOperation;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.dao.query.AnswerQueryCreator;
import qa.dao.query.QuestionQueryCreator;
import qa.dao.query.convertor.AnswerQueryResultConvertor;
import qa.dao.query.convertor.QuestionQueryResultConvertor;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.QuestionView;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.question.QuestionWithCommentsDto;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryConfigurer;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionDao extends DaoImpl<Question> implements Likeable<Long> {

    private final SessionFactory sessionFactory;
    private final JedisResourceCenter jedisResourceCenter;

    private static final QuestionToLikeSetOperation questionToLikeOperation;
    private static final UserQuestionLikeSetOperation userToQuestionLikeOperation;

    static {
        questionToLikeOperation = new QuestionToLikeSetOperation();
        userToQuestionLikeOperation = new UserQuestionLikeSetOperation();
    }

    @Autowired
    public QuestionDao(PropertySetterFactory propertySetterFactory,
                       SessionFactory sessionFactory,
                       JedisResourceCenter jedisResourceCenter) {
        super(HibernateSessionFactoryConfigurer.getSessionFactory(), new Question(), propertySetterFactory.getSetter(new Question()));
        this.sessionFactory = sessionFactory;
        this.jedisResourceCenter = jedisResourceCenter;
    }

    @Override
    public Long create(Question e) {
        final Long id = (Long) super.create(e);
        this.createLike(id);
        return id;
    }

    public void delete(long questionId) {
        final Where where = new Where("id", questionId, WhereOperator.EQUALS);
        super.delete(where);
        this.deleteLikes(questionId);
    }

    public boolean isExist(Long id) {
        return super.isExist(new Where("id", id, WhereOperator.EQUALS), "Question");
    }

    @Nullable
    public Question getFullQuestion(long questionId, long userId) { // FIXME nested caches & nested delete

        /*
         *  if question not exist: - return null
         *  if answers not exist: NullResultException - return question with answers (empty list)
         */
        final QuestionWithCommentsDto questionResult;
        final Question question;
        List<Answer> answers = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            questionResult = QuestionQueryCreator
                    .questionWithCommentsQuery(session, questionId)
                    .uniqueResult();
            if (questionResult == null) {
                transaction.rollback();
                return null;
            }

            try {
                answers = getAnswersWithComment(session, questionId);
            } catch (NullResultException ignored) {}
            transaction.commit();
        }

        question = QuestionQueryResultConvertor.dtoToQuestion(questionResult, questionId);
        question.setAnswers(answers);
        setLike(question, userId);
        return question;
    }

    @NotNull
    public List<QuestionView> getQuestionViewsDto(int page) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<QuestionView> views = QuestionQueryResultConvertor
                    .dtoToQuestionViewList(QuestionQueryCreator
                            .questionsViewsQuery(session, page)
                            .list()
                    );
            transaction.commit();
            setLikes(views); // FIXME TODO cache provider
            return views;
        }
    }

    @Override
    public void like(long userId, Long id) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();

            final boolean status = userToQuestionLikeOperation.add(userId, id, jedis);
            if (status) questionToLikeOperation.increment(id, jedis);
        }
    }

    private void createLike(long questionId) {
        try(JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            LikesUtil.createLike(questionId, questionToLikeOperation, jedis);
        }
    }

    private void setLike(Question question, long userId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            LikesUtil.setLikeAndLiked(
                    question,
                    userId,
                    questionToLikeOperation,
                    userToQuestionLikeOperation,
                    jedis
            );
        }
    }

    private void setLikes(List<QuestionView> questionViews) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            LikesUtil.setLikes(questionViews, questionToLikeOperation, jedis);
        }
    }

    private void deleteLikes(long questionId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();

            questionToLikeOperation.delete(questionId, jedis);
            userToQuestionLikeOperation.deleteEntity(questionId, jedis);
        }
    }

    private List<Answer> getAnswersWithComment(Session session, long questionId) {
        return AnswerQueryResultConvertor
                .dtoToAnswerList(
                        AnswerQueryCreator
                                .answersWithCommentsQuery(session, questionId)
                                .list()
                );
    }
}
