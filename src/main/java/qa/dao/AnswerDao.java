package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.domain.Answer;
import qa.domain.setters.PropertySetterFactory;
import qa.util.hibernate.HibernateSessionFactoryUtil;

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
        try(Session session = sessionFactory.openSession()) {
            Query<?> query = session.createQuery("select id from Answer where id=:a").setParameter("a", id);
            Transaction transaction = session.beginTransaction();
            Object result = query.uniqueResult();
            transaction.commit();
            return result != null;
        }
    }
}
