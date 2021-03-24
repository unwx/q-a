package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.domain.Question;
import qa.domain.setters.PropertySetterFactory;
import qa.util.hibernate.HibernateSessionFactoryUtil;

@Component
public class QuestionDao extends DaoImpl<Question> {

    private final SessionFactory sessionFactory;

    @Autowired
    public QuestionDao(PropertySetterFactory propertySetterFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new Question(), propertySetterFactory.getSetter(new Question()));
        this.sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    }

    @Override
    public Long create(Question e) {
        return (Long) super.create(e);
    }

    public boolean isExist(Long id) {
        try(Session session = sessionFactory.openSession()) {
            Query<?> query = session.createQuery("select id from Question where id=:a").setParameter("a", id);
            Transaction transaction = session.beginTransaction();
            Object result = query.uniqueResult();
            transaction.commit();
            return result != null;
        }
    }
}
