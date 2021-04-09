package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.dao.query.AnswerQueryFactory;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.setters.PropertySetterFactory;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnswerDao extends DaoImpl<Answer> {

    private final SessionFactory sessionFactory;
    private final AnswerQueryFactory answerQueryFactory;

    @Autowired
    public AnswerDao(PropertySetterFactory propertySetterFactory,
                     AnswerQueryFactory answerQueryFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new Answer(), propertySetterFactory.getSetter(new Answer()));
        this.answerQueryFactory = answerQueryFactory;
        this.sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    }

    @Override
    public Long create(Answer e) {
        return (Long) super.create(e);
    }

    public boolean isExist(Long id) {
        return super.isExist(new Where("id", id, WhereOperator.EQUALS));
    }

    @Nullable
    public List<Answer> getAnswers(long questionId, int page) {

        /*
         *  if question not exist: answers.size() = 0; (NullResultException will not be thrown) - return null
         *  if answers not exist: NullResultException - return empty list.
         *  if exist: return result.
         */

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Answer> answers = new ArrayList<>();

            try {
                answers = answerQueryFactory
                        .getConvertor()
                        .dtoToAnswerList(answerQueryFactory
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
            return answers;
        }
    }

    @Nullable
    public List<CommentAnswer> getAnswerComments(long answerId, int page) {

        /*
         *  if answer not exist: answers.size() = 0; (NullResultException will not be thrown) - return null
         *  if comments not exist: NullResultException - return empty list.
         *  if exist: return result.
         */

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<CommentAnswer> comments = new ArrayList<>();

            try {
                comments = answerQueryFactory
                        .getConvertor()
                        .dtoToCommentAnswerList(answerQueryFactory
                                .answerCommentsQuery(session, answerId, page)
                                .list()
                        );
            }
            catch (NullResultException ex) {
                transaction.rollback();
                return comments;
            }

            if (comments.isEmpty()) {
                transaction.rollback();
                return null;
            }

            transaction.commit();
            return comments;
        }
    }
}
