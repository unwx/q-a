package qa.dao;

import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.List;

@Component
public class UserDao implements Dao<User, Long> {

    SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final DaoImpl<User> dao;

    @Autowired
    public UserDao(PropertySetterFactory propertySetterFactory) {
         dao = new DaoImpl<>(
                sessionFactory,
                new User(),
                 propertySetterFactory.getSetter(new User()));
    }

    @Override
    public Long create(@NotNull final User user) {
        return (Long) dao.create(user);
    }

    @Override
    @Nullable
    public User read(@NotNull final Where where, @NotNull final Table target) {
        return dao.read(where, target);
    }

    @Override
    @Nullable
    public User read(@NotNull final Where where, @NotNull final Table target, @NotNull final List<NestedEntity> nested) {
        return dao.read(where, target, nested);
    }

    @Override
    public List<User> readMany(@NotNull final Where where, @NotNull final Table target) {
        return dao.readMany(where, target);
    }

    @Override
    public void update(@NotNull final Where where, @NotNull final User user, @NotNull final String clz) {
        dao.update(where, user, clz);
    }

    @Override
    public void updateEager(User user) {
        dao.updateEager(user);
    }

    @Override
    public void delete(@NotNull final Class<User> clazz, @NotNull final Where where) {
        dao.delete(clazz, where);
    }
}
