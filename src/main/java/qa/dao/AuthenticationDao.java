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
import qa.domain.UserRole;
import qa.domain.setters.PropertySetterFactory;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.math.BigInteger;
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
                    SELECT\s\
                    a.id, a.access_token_exp_date, a.refresh_token_exp_date, ur.roles\s\
                    FROM authentication AS a\s\
                    INNER JOIN user_role AS ur ON a.id = ur.auth_id\s\
                    WHERE a.email=:a
                    """;
            Query<?> query = session.createSQLQuery(hql).setParameter("a", email);
            List<Object[]> result = (List<Object[]>) query.list();

            transaction.commit();
            if (result.size() == 0)
                return null;

            return new AuthenticationData.Builder()
                    .id(((BigInteger) result.get(0)[0]).longValue())
                    .accessTokenExpirationDateAtMillis(((BigInteger) result.get(0)[1]).longValue())
                    .refreshTokenExpirationDateAtMillis(((BigInteger) result.get(0)[2]).longValue())
                    .roles(convertStringToRoles((String) result.get(0)[3]))
                    .email(email)
                    .build();
        }
    }

    private List<UserRole> convertStringToRoles(String rolesStr) {
        List<UserRole> roles = new LinkedList<>();
        for (String s : rolesStr.split(",")) {
            switch (s) {
                case "USER" -> roles.add(UserRole.USER);
                case "MODERATOR" -> roles.add(UserRole.MODERATOR);
                case "ADMIN" -> roles.add(UserRole.ADMIN);
            }
        }
        return roles;
    }
}
