package qa.util.dao.query.builder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.TestLogger;
import qa.util.dao.query.params.CommentQueryParameters;

import java.util.Date;

public class CommentAnswerQueryBuilder implements SessionInitializer {

    private Session session;
    private static final Logger logger = LogManager.getLogger(CommentAnswerQueryBuilder.class);

    @Override
    public CommentAnswerQueryBuilder with(Session session) {
        this.session = session;
        return this;
    }

    public void commentAnswer(Long id,
                              String text,
                              Long questionId,
                              Date date) {
        createCommentAnswerQuery(
                id,
                text,
                questionId,
                date,
                session)
                .executeUpdate();
    }

    public void commentAnswer(Long id,
                              Date date,
                              Long answerId) {
        createCommentAnswerQuery(
                id,
                CommentQueryParameters.TEXT,
                answerId,
                date,
                session)
                .executeUpdate();
    }

    public void commentAnswer(Long id,
                              Date date) {
        createCommentAnswerQuery(
                id,
                CommentQueryParameters.TEXT,
                CommentQueryParameters.ANSWER_ID,
                date,
                session)
                .executeUpdate();
    }

    private Query<?> createCommentAnswerQuery(Long id,
                                              String text,
                                              Long answerId,
                                              Date date,
                                              Session session) {
        TestLogger.trace(logger, "create comment-answer query", 0);
        String sql =
                """
                INSERT INTO comment (comment_type, id, text, author_id, answer_id, question_id, creation_date)\s\
                VALUES ('answer', :id, :text, 1, :answerId, null, :date)\
                """;
        return session.createSQLQuery(sql)
                .setParameter("id", id)
                .setParameter("text", text)
                .setParameter("answerId", answerId)
                .setParameter("date", date);
    }
}
