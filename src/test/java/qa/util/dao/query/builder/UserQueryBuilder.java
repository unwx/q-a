package qa.util.dao.query.builder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.TestLogger;
import qa.util.dao.query.params.UserQueryParameters;

public class UserQueryBuilder implements SessionInitializer {

    private Session session;
    private static final Logger logger = LogManager.getLogger(UserQueryBuilder.class);

    @Override
    public UserQueryBuilder with(Session session) {
        this.session = session;
        return this;
    }

    public void user(Long id, String username) {
        createUserQuery(id, username, session).executeUpdate();
    }

    public void user() {
        createUserQuery(1L, UserQueryParameters.USERNAME, session).executeUpdate();
    }

    private Query<?> createUserQuery(Long id, String username, Session session) {
        TestLogger.trace(logger, "create user query", 0);
        String sql =
                """
                INSERT INTO usr (id, about, username)\s\
                VALUES (:id, 'about', :username)\
                """;
        return session.createSQLQuery(sql)
                .setParameter("id", id)
                .setParameter("username", username);
    }
}
