package qa.util.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import qa.TestLogger;
import qa.util.dao.query.builder.QueryBuilder;

public class UserDaoTestUtil {

    public static final int RESULT_SIZE = 10;

    private final QueryBuilder queryBuilder;

    private static final Logger logger = LogManager.getLogger(UserDaoTestUtil.class);

    public UserDaoTestUtil(SessionFactory sessionFactory) {
        this.queryBuilder = new QueryBuilder(sessionFactory);
    }

    public void createUser() {
        TestLogger.trace(logger, "create user", 2);
        queryBuilder
                .openSession()
                .user()
                .closeSession();
    }
}
