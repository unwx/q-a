package qa.util.dao.query.builder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import qa.TestLogger;

import java.util.Date;

public class QueryBuilder {

    private final SessionFactory sessionFactory;
    private Session session;

    private final UserQueryBuilder userQueryBuilder = new UserQueryBuilder();
    private final QuestionQueryBuilder questionQueryBuilder = new QuestionQueryBuilder();
    private final AnswerQueryBuilder answerQueryBuilder = new AnswerQueryBuilder();
    private final CommentQuestionQueryBuilder commentQuestionQueryBuilder = new CommentQuestionQueryBuilder();
    private final CommentAnswerQueryBuilder commentAnswerQueryBuilder = new CommentAnswerQueryBuilder();

    private static final Logger logger = LogManager.getLogger(QueryBuilder.class);

    public QueryBuilder(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public QueryBuilder openSession() {
        TestLogger.trace(logger, "open session", 1);
        this.session = sessionFactory.openSession();
        this.session.beginTransaction();
        return this;
    }

    public void closeSession() {
        TestLogger.trace(logger, "close session", 1);
        this.session.getTransaction().commit();
        this.session.close();
    }

    public QueryBuilder flushAndClear() {
        TestLogger.trace(logger, "flush and clear", 1);
        this.session.flush();
        this.session.clear();
        return this;
    }

    public QueryBuilder user(Long id, String username) {
        TestLogger.trace(logger, "user", 1);
        userQueryBuilder
                .with(session)
                .user(id, username);
        return this;
    }

    public QueryBuilder user() {
        TestLogger.trace(logger, "build user", 1);
        userQueryBuilder
                .with(session)
                .user();
        return this;
    }

    public QueryBuilder question(Long id,
                                 Date date,
                                 String tags,
                                 String text,
                                 String title) {
        TestLogger.trace(logger, "build question", 1);
        questionQueryBuilder
                .with(session)
                .question(id, date, tags, text, title);
        return this;
    }

    public QueryBuilder question(Long id,
                                 Date date) {
        TestLogger.trace(logger, "build question", 1);

        questionQueryBuilder
                .with(session)
                .question(id, date);
        return this;
    }

    public QueryBuilder question(Long id) {
        TestLogger.trace(logger, "build question", 1);

        questionQueryBuilder
                .with(session)
                .question(id);
        return this;
    }

    public QueryBuilder question() {
        TestLogger.trace(logger, "build question", 1);

        questionQueryBuilder
                .with(session)
                .question();
        return this;
    }

    public QueryBuilder answer(Long id,
                               Boolean answered,
                               Date date,
                               String text,
                               Long questionId) {
        TestLogger.trace(logger, "build answer", 1);
        answerQueryBuilder
                .with(session)
                .answer(id, answered, date, text, questionId);
        return this;
    }

    public QueryBuilder answer(Long id,
                               Boolean answered,
                               Date date) {
        TestLogger.trace(logger, "build answer", 1);
        answerQueryBuilder
                .with(session)
                .answer(id, answered, date);
        return this;
    }

    public QueryBuilder answer(Long id,
                               Boolean answered) {
        TestLogger.trace(logger, "build answer", 1);
        answerQueryBuilder
                .with(session)
                .answer(id, answered);
        return this;
    }

    public QueryBuilder answer(Long id,
                               Long questionId,
                               Date date) {
        TestLogger.trace(logger, "build answer", 1);
        answerQueryBuilder
                .with(session)
                .answer(id, questionId, date);
        return this;
    }

    public QueryBuilder answer(Long id,
                               Date date) {
        TestLogger.trace(logger, "build answer", 1);
        answerQueryBuilder
                .with(session)
                .answer(id, date);
        return this;
    }

    public QueryBuilder answer(Long id) {
        TestLogger.trace(logger, "build answer", 1);
        answerQueryBuilder
                .with(session)
                .answer(id);
        return this;
    }

    public QueryBuilder answer() {
        TestLogger.trace(logger, "build answer", 1);
        answerQueryBuilder
                .with(session)
                .answer();
        return this;
    }

    public QueryBuilder commentQuestion(Long id,
                                        String text,
                                        Long questionId,
                                        Date date) {
        TestLogger.trace(logger, "build comment-question", 1);
        commentQuestionQueryBuilder
                .with(session)
                .commentQuestion(id, text, questionId, date);
        return this;
    }

    public QueryBuilder commentQuestion(Long id,
                                        Date date) {
        TestLogger.trace(logger, "build comment-question", 1);
        commentQuestionQueryBuilder
                .with(session)
                .commentQuestion(id, date);
        return this;
    }

    public QueryBuilder commentAnswer(Long id,
                                      String text,
                                      Long answerId,
                                      Date date) {
        TestLogger.trace(logger, "build comment-question", 1);
        commentAnswerQueryBuilder
                .with(session)
                .commentAnswer(id, text, answerId, date);
        return this;
    }

    public QueryBuilder commentAnswer(Long id,
                              Long answerId,
                              Date date) {
        TestLogger.trace(logger, "build comment-answer", 1);
        commentAnswerQueryBuilder
                .with(session)
                .commentAnswer(id, date, answerId);
        return this;
    }

    public QueryBuilder commentAnswer(Long id,
                              Date date) {
        TestLogger.trace(logger, "build comment-answer", 1);
        commentAnswerQueryBuilder
                .with(session)
                .commentAnswer(id, date);
        return this;
    }
}
