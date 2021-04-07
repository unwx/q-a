package qa.util.dao;

import org.hibernate.SessionFactory;
import qa.util.dao.query.builder.QueryBuilder;

public class AnswerDaoTestUtil {

    private final QueryBuilder queryBuilder;

    public AnswerDaoTestUtil(SessionFactory sessionFactory) {
        this.queryBuilder = new QueryBuilder(sessionFactory);
    }

    public void createAnswer() {
        queryBuilder
                .openSession()
                .user()
                .question()
                .answer()
                .closeSession();
    }
}
