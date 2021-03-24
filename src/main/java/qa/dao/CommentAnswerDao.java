package qa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.domain.CommentAnswer;
import qa.domain.setters.PropertySetterFactory;
import qa.util.hibernate.HibernateSessionFactoryUtil;

@Component
public class CommentAnswerDao extends DaoImpl<CommentAnswer> {

    @Autowired
    public CommentAnswerDao(PropertySetterFactory propertySetterFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new CommentAnswer(), propertySetterFactory.getSetter(new CommentAnswer()));
    }

    @Override
    public Long create(CommentAnswer e) {
        return (Long) super.create(e);
    }
}
