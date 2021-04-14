package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.dao.query.AnswerQueryCreator;
import qa.dao.query.convertor.AnswerQueryResultConvertor;
import qa.domain.Answer;
import qa.domain.setters.PropertySetterFactory;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnswerDao extends DaoImpl<Answer> {

    private final SessionFactory sessionFactory;

    @Autowired
    public AnswerDao(PropertySetterFactory propertySetterFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new Answer(), propertySetterFactory.getSetter(new Answer()));
        this.sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    }

    @Override
    public Long create(Answer e) {
        return (Long) super.create(e);
    }

    public boolean isExist(Long id) {
        return super.isExist(new Where("id", id, WhereOperator.EQUALS), "Answer");
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
            return answers;
        }
    }
}
