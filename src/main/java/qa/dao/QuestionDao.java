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
import qa.cache.like.Likeable;
import qa.cache.like.QuestionLikesProvider;
import qa.dao.database.components.Where;
import qa.dao.database.components.WhereOperator;
import qa.dao.query.manager.AnswerQueryManager;
import qa.dao.query.manager.QuestionQueryManager;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.QuestionView;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.entities.answer.AnswerFullDto;
import qa.dto.internal.hibernate.entities.question.QuestionFullStringIdsDto;
import qa.dto.internal.hibernate.entities.question.QuestionViewDto;
import qa.dto.internal.hibernate.entities.question.QuestionWithCommentsDto;
import qa.exceptions.dao.NullResultException;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class QuestionDao extends DaoImpl<Question> implements Likeable<Long> {

    private final SessionFactory sessionFactory;
    private final JedisResourceCenter jedisResourceCenter;
    private final QuestionLikesProvider likesProvider;

    @Autowired
    public QuestionDao(PropertySetterFactory propertySetterFactory,
                       SessionFactory sessionFactory,
                       JedisResourceCenter jedisResourceCenter,
                       QuestionLikesProvider likesProvider) {

        super(sessionFactory, propertySetterFactory.getSetter(new Question()));
        this.sessionFactory = sessionFactory;
        this.jedisResourceCenter = jedisResourceCenter;
        this.likesProvider = likesProvider;
    }

    public boolean isExist(Long id) {
        return super.isExist(new Where("id", id, WhereOperator.EQUALS));
    }

    @Override
    public Long create(Question e) {
        final Long id = (Long) super.create(e);
        this.createLike(id);
        return id;
    }

    @Override
    public void update(Where where, Question entity) {
        entity.setLastActivity(new Date());
        super.update(where, entity);
    }

    public void delete(long questionId) {
        final Where where = new Where("id", questionId, WhereOperator.EQUALS);
        final QuestionFullStringIdsDto questionFullStringIdsDto;

        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            questionFullStringIdsDto = QuestionQueryManager
                    .questionFullIdsQuery(session, questionId)
                    .uniqueResult();

            if (questionFullStringIdsDto == null) {
                transaction.rollback();
                return;
            }

            transaction.commit();
        }
        super.delete(where);
        this.deleteLikes(questionFullStringIdsDto, questionId);
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
            final Transaction transaction = session.beginTransaction();

            questionResult = QuestionQueryManager
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

        question = QuestionQueryManager.dtoToQuestion(questionResult, questionId);
        question.setAnswers(answers);
        setLike(question, userId);
        return question;
    }

    @NotNull
    public List<QuestionView> getQuestionViewsDto(int page) {

        final List<QuestionViewDto> dto;
        final List<QuestionView> views;

        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            dto = QuestionQueryManager
                    .questionsViewsQuery(session, page)
                    .list();

            transaction.commit();
        }

        views = QuestionQueryManager.dtoToQuestionViewList(dto);
        setLikes(views);
        return views;
    }

    public long getQuestionAuthorIdFromAnswer(long answerId) {
        final Long result;

        try(Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            result = QuestionQueryManager
                    .questionAuthorIdFromAnswerQuery(session, answerId)
                    .uniqueResult();

            transaction.commit();
        }

        return result == null ? -1 : result;
    }

    @Override
    public void like(long userId, Long questionId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            this.likesProvider.like(userId, questionId, jedis);
        }
    }

    private void createLike(long questionId) {
        try(JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            this.likesProvider.initLike(questionId, jedis);
        }
    }

    private void setLike(Question question, long userId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            this.likesProvider.provide(question, userId, jedis);
        }
    }

    private void setLikes(List<QuestionView> questionViews) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            this.likesProvider.provide(questionViews, jedis);
        }
    }

    private void deleteLikes(QuestionFullStringIdsDto dto, long questionId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            this.likesProvider.remove(dto, questionId, jedis);
        }
    }

    private List<Answer> getAnswersWithComment(Session session, long questionId) {
        final List<AnswerFullDto> dto = AnswerQueryManager
                .answersWithCommentsQuery(session, questionId)
                .list();

        return AnswerQueryManager.dtoToAnswerList(dto);
    }
}