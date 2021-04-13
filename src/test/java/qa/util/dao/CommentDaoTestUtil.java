package qa.util.dao;

import org.hibernate.SessionFactory;
import qa.util.dao.query.builder.QueryBuilder;

public class CommentDaoTestUtil {

    private final QueryBuilder queryBuilder;

    public CommentDaoTestUtil(SessionFactory sessionFactory) {
        this.queryBuilder = new QueryBuilder(sessionFactory);
    }

    public void createCommentAnswer() {
        queryBuilder
                .openSession()
                .user()
                .question()
                .answer()
                .commentAnswer()
                .closeSession();
    }

    public void createCommentQuestion() {
        queryBuilder
                .openSession()
                .user()
                .question()
                .answer()
                .commentQuestion()
                .closeSession();
    }

    public void createCommentAnswerNoUser() {
        queryBuilder
                .openSession()
                .question()
                .answer()
                .commentAnswer()
                .closeSession();
    }

    public void createCommentQuestionNoUser() {
        queryBuilder
                .openSession()
                .question()
                .answer()
                .commentQuestion()
                .closeSession();
    }
}
