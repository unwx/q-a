package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;
import qa.domain.AuthenticationData;
import qa.domain.setters.AuthenticationDataSetter;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.List;

@Component
public class AuthenticationDao implements Dao<AuthenticationData, Long> {

    SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final DaoImpl<AuthenticationData> dao = new DaoImpl<>(
            sessionFactory,
            new AuthenticationData(),
            AuthenticationDataSetter.getInstance());

    @Override
    public Long create(@NotNull final AuthenticationData data) {
        return (Long) dao.create(data);
    }

    @Override
    @Nullable
    public AuthenticationData read(@NotNull final Where where, @NotNull final Table target) {
        return dao.read(where, target);
    }

    @Override
    @Nullable
    public AuthenticationData read(@NotNull final Where where, @NotNull final Table target, @NotNull final List<NestedEntity> nested) {
        return dao.read(where, target, nested);
    }

    @Override
    public List<AuthenticationData> readMany(@NotNull final Where where, @NotNull final Table target) {
        return dao.readMany(where, target);
    }

    @Override
    public void update(@NotNull final Where where, @NotNull final AuthenticationData data, @NotNull final String className) {
        dao.update(where, data, className);
    }

    @Override
    public void updateEager(AuthenticationData data) {
        dao.updateEager(data);
    }

    @Override
    public void delete(@NotNull final AuthenticationData data) {
        dao.delete(data);
    }

    public boolean isEmailPasswordCorrect(String email, String password) {
        try(Session session = sessionFactory.openSession()) {
            String hql = "select a.id from AuthenticationData a where a.email=:a and a.password=:b";
            Transaction transaction = session.beginTransaction();
            Object obj = session.createQuery(hql)
                    .setParameter("a", email)
                    .setParameter("b", password)
                    .uniqueResult();
            transaction.commit();
            return obj != null;
        }
    }
}
