package qa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.domain.CommentQuestion;
import qa.domain.setters.PropertySetterFactory;
import qa.util.hibernate.HibernateSessionFactoryUtil;

@Component
public class CommentQuestionDao extends DaoImpl<CommentQuestion> {

    @Autowired
    public CommentQuestionDao(PropertySetterFactory propertySetterFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new CommentQuestion(), propertySetterFactory.getSetter(new CommentQuestion()));
    }

    @Override
    public Long create(CommentQuestion e) {
        return (Long) super.create(e);
    }
}
