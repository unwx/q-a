package qa.util.dao;

import org.hibernate.SessionFactory;
import qa.util.dao.query.builder.QueryBuilder;

public class UserDaoTestUtil {

    public static final int RESULT_SIZE = 15;

    private final QueryBuilder queryBuilder;

    public UserDaoTestUtil(SessionFactory sessionFactory) {
        this.queryBuilder = new QueryBuilder(sessionFactory);
    }

    public void createUser() {
        queryBuilder
                .openSession()
                .user()
                .closeSession();
    }
}
