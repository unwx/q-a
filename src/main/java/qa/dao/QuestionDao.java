package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.dao.query.AnswerQueryFactory;
import qa.dao.query.QuestionQueryFactory;
import qa.domain.Answer;
import qa.domain.CommentQuestion;
import qa.domain.Question;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.question.QuestionViewDto;
import qa.dto.internal.hibernate.question.QuestionWithCommentsDto;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionDao extends DaoImpl<Question> {

    private final SessionFactory sessionFactory;
    private final QuestionQueryFactory questionQueryFactory;
    private final AnswerQueryFactory answerQueryFactory;

    @Autowired
    public QuestionDao(PropertySetterFactory propertySetterFactory,
                       QuestionQueryFactory questionQueryFactory,
                       AnswerQueryFactory answerQueryFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new Question(), propertySetterFactory.getSetter(new Question()));
        this.questionQueryFactory = questionQueryFactory;
        this.answerQueryFactory = answerQueryFactory;
        this.sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    }

    @Override
    public Long create(Question e) {
        return (Long) super.create(e);
    }

    public boolean isExist(Long id) {
        return super.isExist(new Where("id", id, WhereOperator.EQUALS));
    }

    @Nullable
    public Question getFullQuestion(long questionId) {

        /*
         *  if question not exist: - return null
         *  if answers not exist: NullResultException - return question with answers (empty list)
         */

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            QuestionWithCommentsDto questionResult = questionQueryFactory
                    .questionWithCommentsQuery(session, questionId)
                    .uniqueResult();
            if (questionResult == null) {
                transaction.rollback();
                return null;
            }

            List<Answer> answers = new ArrayList<>();
            try {
                answers = answerQueryFactory.getConvertor()
                        .dtoToAnswerList(
                                answerQueryFactory
                                        .answersWithCommentsQuery(session, questionId)
                                        .list()
                        );
            } catch (NullResultException ignored) {}

            transaction.commit();
            Question question = questionQueryFactory
                    .getConvertor()
                    .dtoToQuestion(questionResult, questionId);
            question.setAnswers(answers);
            return question;
        }
    }

    @NotNull
    public List<QuestionViewDto> getQuestionViewsDto(int page) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<QuestionViewDto> views = questionQueryFactory
                    .questionsViewsQuery(session, page)
                    .list();
            transaction.commit();
            return views;
        }
    }

    @Nullable
    public List<CommentQuestion> getQuestionComments(long questionId, int page) {

        /*
         *  if question not exist: comments.size() = 0; (NullResultException will not be thrown) - return null
         *  if comments not exist: NullResultException - return empty list.
         *  if exist: return result.
         */

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<CommentQuestion> comments = new ArrayList<>();

            try {
                comments = questionQueryFactory
                        .getConvertor()
                        .dtoToCommentQuestionList(questionQueryFactory
                                .questionCommentsQuery(session, questionId, page)
                                .list()
                        );
            } catch (NullResultException ex) { // comments not exist
                transaction.rollback();
                return comments;
            }

            if (comments.isEmpty()) { // question not exist
                transaction.rollback();
                return null;
            }

            transaction.commit();
            return comments;
        }
    }
}
