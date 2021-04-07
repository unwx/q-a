package qa.util.rest;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import qa.security.jwt.entity.JwtData;
import qa.security.jwt.service.JwtProvider;
import qa.util.dao.query.params.UserQueryParameters;

public class JwtTestUtil {

    public final static String USER_PASSWORD = "ho3kLS4hl2dp-asd";
    public final static String USER_EMAIL = "yahoo@yahoo.com";
    public final static String USER_SECOND_EMAIL = "second@yahoo.com";

    private JwtTestUtil() {
    }

    public static String createUserWithToken(SessionFactory sessionFactory, JwtProvider jwtProvider) {
        ImmutablePair<String, Long> pair = createToken(USER_EMAIL, jwtProvider);
        String token = pair.left;
        Long exp = pair.right;

        createAuthenticationWithUserWithRoles(
                1L,
                UserQueryParameters.USERNAME,
                USER_EMAIL,
                exp,
                USER_PASSWORD,
                sessionFactory);
        return token;
    }

    public static String createSecondUserWithToken(SessionFactory sessionFactory, JwtProvider jwtProvider) {
        ImmutablePair<String, Long> pair = createToken(USER_SECOND_EMAIL, jwtProvider);
        String token = pair.left;
        Long exp = pair.right;

        createAuthenticationWithUserWithRoles(
                2L,
                UserQueryParameters.SECOND_USERNAME,
                USER_SECOND_EMAIL,
                exp,
                USER_PASSWORD,
                sessionFactory);
        return token;
    }


    public static void createAuthenticationWithUserWithRoles(Long id,
                                                             String username,
                                                             String email,
                                                             Long exp,
                                                             String password,
                                                             SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            createUserQuery(id, username, session).executeUpdate();
            createAuthenticationQuery(id, email, exp, password, session).executeUpdate();
            createRolesQuery(id, session).executeUpdate();
            transaction.commit();
        }
    }

    public static Query<?> createAuthenticationQuery(Long id, String email, Long exp, String password, Session session) {
        String sql =
                """
                INSERT INTO authentication (id, access_token_exp_date, email, enabled, password, refresh_token_exp_date, user_id)\s\
                VALUES (:id, :exp, :email, true, :password, 1, :id)\
                """;
        return session.createSQLQuery(sql)
                .setParameter("id", id)
                .setParameter("email", email)
                .setParameter("exp", exp)
                .setParameter("password", password);
    }

    public static Query<?> createRolesQuery(Long id, Session session) {
        String sql =
                """
                INSERT INTO user_role (auth_id, roles)\s\
                VALUES (:id, 'USER')\
                """;
        return session.createSQLQuery(sql)
                .setParameter("id", id);
    }

    public static Query<?> createUserQuery(Long id, String username, Session session) {
        String sql =
                """
                INSERT INTO usr (id, about, username)\s\
                VALUES (:id, 'about', :username)\
                """;
        return session.createSQLQuery(sql)
                .setParameter("id", id)
                .setParameter("username", username);
    }

    private static ImmutablePair<String, Long> createToken(String email, JwtProvider jwtProvider) {
        JwtData data = jwtProvider.createAccess(email);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        return new ImmutablePair<>(token, exp);
    }
}
