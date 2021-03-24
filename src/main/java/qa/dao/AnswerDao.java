package qa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.domain.Answer;
import qa.domain.setters.PropertySetterFactory;
import qa.util.hibernate.HibernateSessionFactoryUtil;

@Component
public class AnswerDao extends DaoImpl<Answer> {

    @Autowired
    public AnswerDao(PropertySetterFactory propertySetterFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new Answer(), propertySetterFactory.getSetter(new Answer()));
    }

    @Override
    public Long create(Answer e) {
        return (Long) super.create(e);
    }
}
