package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.domain.AuthenticationData;
import qa.domain.UserRoles;
import qa.domain.setters.PropertySetterFactory;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.LinkedList;
import java.util.List;

@Component
public class AuthenticationDao extends DaoImpl<AuthenticationData> {

    private final SessionFactory sessionFactory;

    @Autowired
    public AuthenticationDao(PropertySetterFactory propertySetterFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new AuthenticationData(), propertySetterFactory.getSetter(new AuthenticationData()));
        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    }

    @Override
    public Long create(AuthenticationData e) {
        return (Long) super.create(e);
    }

    public boolean isEmailPasswordCorrect(String email, String password, PooledPBEStringEncryptor passwordEncryptor) {
        try(Session session = sessionFactory.openSession()) {
            String hql = "select a.password from AuthenticationData a where a.email=:a";
            Transaction transaction = session.beginTransaction();
            String pass = (String) session.createQuery(hql)
                    .setParameter("a", email)
                    .uniqueResult();
            transaction.commit();
            if (pass == null)
                return false;

            return passwordEncryptor.decrypt(pass).equals(password);
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
