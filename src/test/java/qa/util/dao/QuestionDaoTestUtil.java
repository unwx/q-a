package qa.util.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import qa.TestLogger;
import qa.util.dao.query.builder.QueryBuilder;

import java.util.Date;

public class QuestionDaoTestUtil {

    private static final long dateAtMillisDefault = 360000000000L;

    public static final int COMMENT_RESULT_SIZE = 3;
    public static final int RESULT_SIZE = 6;
    public static final int QUESTION_VIEW_RESULT_SIZE = 20;

    private final QueryBuilder queryBuilder;

    private static final Logger logger = LogManager.getLogger(QuestionDaoTestUtil.class);

    public QuestionDaoTestUtil(SessionFactory sessionFactory) {
        this.queryBuilder = new QueryBuilder(sessionFactory);
    }

    public void createQuestionWithCommentsAndAnswersWithComments(int answers, int comments) {
        TestLogger.trace(logger, "create full question", 2);
        queryBuilder
                .openSession()
                .user()
                .question();

        long commentId = answers;
        for (int i = 0; i < answers; i++) {
            queryBuilder
                    .answer((long) i, new Date(i * dateAtMillisDefault))
                    .commentQuestion((long) i, new Date(i * dateAtMillisDefault))
                    .flushAndClear();
            for (int y = 0; y < comments; y++) {
                queryBuilder.commentAnswer(commentId, (long) i, new Date(y + dateAtMillisDefault));
                commentId++;
            }
        }
        queryBuilder.closeSession();
    }

    public void createQuestionWithComments(int comments) {
        TestLogger.trace(logger, "create question with comments", 2);
        queryBuilder
                .openSession()
                .user()
                .question();

        for (int i = 0; i < comments; i++) {
            queryBuilder.commentQuestion((long) i, new Date(dateAtMillisDefault * i));
            if (i % 20 == 0)
                queryBuilder.flushAndClear();
        }
        queryBuilder.closeSession();
    }

    public void createQuestionWithAnswersWithComments(int answers, int comments) {
        TestLogger.trace(logger, "create question with answers with comments", 2);
        queryBuilder
                .openSession()
                .user()
                .question();

        long commentId = 0;
        for (int i = 0; i < answers; i++) {
            queryBuilder
                    .answer((long) i, new Date(i * dateAtMillisDefault))
                    .flushAndClear();
            for (int y = 0; y < comments; y++) {
                queryBuilder.commentAnswer(commentId, (long) i, new Date(y + dateAtMillisDefault + i * 1000L));
                commentId++;
            }
        }
        queryBuilder.closeSession();
    }

    public void createManyQuestionsWithManyAnswers(int questions, int answers) {
        TestLogger.trace(logger, "create many questions with many answers", 2);
        queryBuilder
                .openSession()
                .user();

        long answerId = 0;
        for (int i = 0; i < questions; i++) {
            queryBuilder
                    .question((long) i, new Date(i * dateAtMillisDefault))
                    .flushAndClear();
            for (int y = 0; y < answers; y++) {
                queryBuilder.answer(answerId, (long) i, new Date(y + dateAtMillisDefault + i * 1000L));
                answerId++;
            }
        }
        queryBuilder.closeSession();
    }

    public void createManyQuestions(int questions) {
        TestLogger.trace(logger, "create many questions", 2);
        queryBuilder
                .openSession()
                .user();
        for (int i = 0; i < questions; i++) {
            queryBuilder.question((long) i, new Date(i * dateAtMillisDefault));
            if (i % 20 == 0) {
                queryBuilder.flushAndClear();
            }
        }
        queryBuilder.closeSession();
    }

    public void createQuestion() {
        TestLogger.trace(logger, "create question", 2);
        queryBuilder
                .openSession()
                .user()
                .question()
                .closeSession();
    }

    public void createQuestionNoUser() {
        TestLogger.trace(logger, "create question no user", 2);
        queryBuilder
                .openSession()
                .question()
                .closeSession();
    }
}
