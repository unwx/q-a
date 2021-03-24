package qa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.util.hibernate.HibernateSessionFactoryUtil;

@Component
public class UserDao extends DaoImpl<User> {

    @Autowired
    public UserDao(PropertySetterFactory propertySetterFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new User(), propertySetterFactory.getSetter(new User()));
    }

    @Override
    public Long create(User e) {
        return (Long) super.create(e);
    }
}
