package qa.util.dao;

import org.hibernate.SessionFactory;
import qa.util.dao.query.builder.QueryBuilder;

import java.util.Date;

public class CommentDaoTestUtil {

    private final QueryBuilder queryBuilder;
    private static final long dateAtMillisDefault = 360000000000L;

    public static final int COMMENT_RESULT_SIZE = 3;

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

    public void createManyCommentQuestions(int comment) {
        queryBuilder
                .openSession()
                .user()
                .question();
        for (int i = 0; i < comment; i++) {
            queryBuilder.commentQuestion((long) i, new Date(dateAtMillisDefault * i));
            if (i % 25 == 0)
                queryBuilder.flushAndClear();
        }
        queryBuilder.closeSession();
    }

    public void createManyCommentAnswers(int comment) {
        queryBuilder
                .openSession()
                .user()
                .question()
                .answer();
        for (int i = 0; i < comment; i++) {
            queryBuilder.commentAnswer((long) i, new Date(dateAtMillisDefault * i));
            if (i % 25 == 0)
                queryBuilder.flushAndClear();
        }
        queryBuilder.closeSession();
    }
}
