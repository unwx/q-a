package qa.util.dao.query.builder;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.util.dao.query.params.CommentQueryParameters;

import java.util.Date;

public class CommentAnswerQueryBuilder implements SessionInitializer {

    private Session session;
    

    @Override
    public CommentAnswerQueryBuilder with(Session session) {
        this.session = session;
        return this;
    }

    public void commentAnswer(long id,
                              long userId,
                              long answerId,
                              String text,
                              Date date) {
        createCommentAnswerQuery(
                id,
                userId,
                text,
                answerId,
                date,
                session
        ).executeUpdate();
    }

    public void commentAnswer(Long id,
                              String text,
                              Long questionId,
                              Date date) {
        createCommentAnswerQuery(
                id,
                1L,
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
                1L,
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
                1L,
                CommentQueryParameters.TEXT,
                CommentQueryParameters.ANSWER_ID,
                date,
                session)
                .executeUpdate();
    }

    public void commentAnswer() {
        createCommentAnswerQuery(
                1L,
                1L,
                CommentQueryParameters.TEXT,
                CommentQueryParameters.ANSWER_ID,
                CommentQueryParameters.DATE,
                session)
                .executeUpdate();
    }

    private Query<?> createCommentAnswerQuery(Long id,
                                              long authorId,
                                              String text,
                                              Long answerId,
                                              Date date,
                                              Session session) {
        String sql =
                """
                INSERT INTO comment (comment_type, id, text, author_id, answer_id, question_id, creation_date)\s\
                VALUES ('answer', :id, :text, :authorId, :answerId, null, :date)\
                """;
        return session.createSQLQuery(sql)
                .setParameter("id", id)
                .setParameter("text", text)
                .setParameter("answerId", answerId)
                .setParameter("date", date)
                .setParameter("authorId", authorId);
    }
}
