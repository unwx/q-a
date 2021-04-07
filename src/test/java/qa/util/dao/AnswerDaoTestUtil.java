package qa.util.dao;

import org.hibernate.SessionFactory;
import qa.util.dao.query.builder.QueryBuilder;

import java.util.Date;

public class AnswerDaoTestUtil {

    private static final long dateAtMillisDefault = 360000000000L;

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

    public void createManyAnswers(int answers) {
        queryBuilder
                .openSession()
                .user()
                .question();

        for (int i = 0; i < answers; i++) {
            queryBuilder.answer((long) i, new Date(dateAtMillisDefault * i));
            if (i % 20 == 0) {
                queryBuilder.flushAndClear();
            }
        }
        queryBuilder.closeSession();
    }
}
