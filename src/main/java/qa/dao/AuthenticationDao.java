package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;
import qa.domain.AuthenticationData;
import qa.domain.UserRoles;
import qa.domain.setters.PropertySetterFactory;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.LinkedList;
import java.util.List;

@Component
public class AuthenticationDao implements Dao<AuthenticationData, Long> {

    SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final DaoImpl<AuthenticationData> dao;

    public AuthenticationDao(PropertySetterFactory propertySetterFactory) {
        dao = new DaoImpl<>(
                sessionFactory,
                new AuthenticationData(),
                propertySetterFactory.getSetter(new AuthenticationData()));
    }

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

    @Nullable
    @SuppressWarnings("unchecked")
    public AuthenticationData getPrincipalWithTokenData(String email) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String hql =
                    """
                    select\s\
                    a.id, a.enabled, a.password, a.accessTokenExpirationDateAtMills, a.refreshTokenExpirationDateAtMillis, b\s\
                    from AuthenticationData a inner join a.roles as b\s\
                    where a.email=:a
                    """;
            Query<?> query = session.createQuery(hql).setParameter("a", email);
            List<Object[]> result = (List<Object[]>) query.list();
            List<UserRoles> roles = new LinkedList<>();

            transaction.commit();
            if (result.size() == 0)
                return null;

            result.forEach((r) -> roles.add((UserRoles) r[5]));
            return new AuthenticationData.Builder()
                    .id((Long) result.get(0)[0])
                    .enabled((Boolean) result.get(0)[1])
                    .password((String) result.get(0)[2])
                    .accessTokenExpirationDateAtMillis((Long) result.get(0)[3])
                    .refreshTokenExpirationDateAtMillis((Long) result.get(0)[4])
                    .roles(roles)
                    .email(email)
                    .build();
        }
    }
}
