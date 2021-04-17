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
import qa.cache.operation.QuestionLikesOperation;
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
public class QuestionDao extends DaoImpl<Question> {

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
        Long id = (Long) super.create(e);
        createLike(id);
        return id;
    }

    public boolean isExist(Long id) {
        return super.isExist(new Where("id", id, WhereOperator.EQUALS), "Question");
    }

    @Nullable
    public Question getFullQuestion(long questionId) {

        /*
         *  if question not exist: - return null
         *  if answers not exist: NullResultException - return question with answers (empty list)
         */
        QuestionWithCommentsDto questionResult;
        Question question;
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
            } catch (NullResultException ignored) {
            }
            transaction.commit();
        }

        question = QuestionQueryResultConvertor.dtoToQuestion(questionResult, questionId);
        question.setAnswers(answers);
        setLike(question);
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

    private void createLike(Long questionId) {
        Long reply;
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            QuestionLikesOperation questionLikesOperation = new QuestionLikesOperation(jedisResource.getJedis());
            reply = questionLikesOperation.create(questionId);
        }
        if (reply == 0)
            throw new EntityAlreadyCreatedException("question already exist. id: %s".formatted(questionId));
    }

    private void setLike(Question question) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            setLikeProcess(question, jedisResource.getJedis());
        }
    }

    private void setLikes(List<QuestionView> questionViews) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            setLikesProcess(questionViews, jedisResource.getJedis());
        }
    }

    private void setLikesProcess(List<QuestionView> questionViews, Jedis jedis) {
        QuestionLikesOperation questionLikesOperation = new QuestionLikesOperation(jedis);
        List<Integer> likes = questionLikesOperation.get(
                questionViews
                        .stream()
                        .map(QuestionView::getId)
                        .collect(Collectors.toList())
        );
        LikesUtil.setLikesProcess(questionViews, likes, questionLikesOperation);
    }

    private void setLikeProcess(Question question, Jedis jedis) {
        QuestionLikesOperation questionLikesOperation = new QuestionLikesOperation(jedis);
        Integer like = questionLikesOperation.get(question.getId());
        LikesUtil.setLikeProcess(question, like, questionLikesOperation);
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
