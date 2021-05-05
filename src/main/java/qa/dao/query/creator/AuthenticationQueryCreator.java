package qa.dao.query.creator;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dto.internal.hibernate.entities.authentication.AuthenticationWithTokensDto;
import qa.dto.internal.hibernate.transformer.authentication.AuthenticationWithTokensDtoResultTransformer;

@SuppressWarnings({"deprecation", "unchecked"})
public class AuthenticationQueryCreator {

    private AuthenticationQueryCreator() {}

    public static Query<?> getPasswordQuery(String email, Session session) {
        final String sql = "SELECT password FROM authentication WHERE email = :email";

        return session.createSQLQuery(sql)
                .unwrap(Query.class)
                .setParameter("email", email);
    }

    public static Query<AuthenticationWithTokensDto> getAuthWithTokensQuery(String email, Session session) {
        final String sql =
                """
                SELECT\s\
                a.id AS aut_id, a.access_token_exp_date AS aut_access_t,\s\
                a.refresh_token_exp_date AS aut_refresh_t, ur.roles as aut_roles\s\
                FROM authentication AS a\s\
                INNER JOIN user_role AS ur ON a.id = ur.auth_id\s\
                WHERE a.email = :email
                """;
        return session.createSQLQuery(sql)
                .unwrap(Query.class)
                .setParameter("email", email)
                .setResultTransformer(new AuthenticationWithTokensDtoResultTransformer());
    }
}
