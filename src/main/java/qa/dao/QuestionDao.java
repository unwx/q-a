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
import qa.cache.operation.impl.UserToQuestionLikeSetOperation;
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
import qa.exceptions.dao.EntityAlreadyCreatedException;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryUtil;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionDao extends DaoImpl<Question> implements Likeable<Long> {

    private final SessionFactory sessionFactory;
    private final JedisResourceCenter jedisResourceCenter;

    @Autowired
    public QuestionDao(PropertySetterFactory propertySetterFactory,
                       JedisResourceCenter jedisResourceCenter) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new Question(), propertySetterFactory.getSetter(new Question()));
        this.jedisResourceCenter = jedisResourceCenter;
        this.sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    }

    @Override
    public Long create(Question e) {
        final Long id = (Long) super.create(e);
        this.createLike(id);
        return id;
    }

    public boolean isExist(Long id) {
        return super.isExist(new Where("id", id, WhereOperator.EQUALS), "Question");
    }

    @Nullable
    public Question getFullQuestion(long questionId, long userId) {

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
            setLikes(views);
            return views;
        }
    }

    @Override
    public void like(long userId, Long id) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            final QuestionToLikeSetOperation questionToLikeSetOperation = new QuestionToLikeSetOperation(jedis);
            final UserToQuestionLikeSetOperation userToQuestionLikeSetOperation = new UserToQuestionLikeSetOperation(jedis);

            final boolean status = userToQuestionLikeSetOperation.add(userId, id);
            if (status) questionToLikeSetOperation.increment(id);
        }
    }

    private void createLike(long questionId) {
        try(JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            final QuestionToLikeSetOperation operation = new QuestionToLikeSetOperation(jedis);
            LikesUtil.createLike(questionId, operation);
        }
    }

    private void setLike(Question question, long userId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            LikesUtil.setLikeAndLiked(
                    question,
                    userId,
                    new QuestionToLikeSetOperation(jedis),
                    new UserToQuestionLikeSetOperation(jedis)
            );
        }
    }

    private void setLikes(List<QuestionView> questionViews) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            LikesUtil.setLikes(questionViews, new QuestionToLikeSetOperation(jedis));
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
