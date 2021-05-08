package util.dao.query.builder;

import org.hibernate.Session;
import org.hibernate.query.Query;
import util.dao.query.params.CommentQueryParameters;

import java.util.Date;

public class CommentQuestionQueryBuilder implements SessionInitializer {

    private Session session;

    @Override
    public CommentQuestionQueryBuilder with(Session session) {
        this.session = session;
        return this;
    }

    public void commentQuestion(long id,
                                long authorId,
                                long questionId,
                                String text,
                                Date date) {
        this.createCommentQuestionQuery(
                id,
                authorId,
                text,
                questionId,
                date,
                session
        ).executeUpdate();
    }

    public void commentQuestion(Long id,
                                String text,
                                Long questionId,
                                Date date) {
        this.createCommentQuestionQuery(
                id,
                1L,
                text,
                questionId,
                date,
                session)
                .executeUpdate();
    }

    public void commentQuestion(Long id,
                                Date date) {
        this.createCommentQuestionQuery(
                id,
                1L,
                CommentQueryParameters.TEXT,
                CommentQueryParameters.QUESTION_ID,
                date,
                session)
                .executeUpdate();
    }

    public void commentQuestion() {
        this.createCommentQuestionQuery(
                1L,
                1L,
                CommentQueryParameters.TEXT,
                CommentQueryParameters.QUESTION_ID,
                CommentQueryParameters.DATE,
                session)
                .executeUpdate();
    }

    private Query<?> createCommentQuestionQuery(Long id,
                                                long authorId,
                                                String text,
                                                Long questionId,
                                                Date date,
                                                Session session) {
        final String sql =
                """
                INSERT INTO comment (comment_type, id, text, author_id, answer_id, question_id, creation_date)\s\
                VALUES ('question', :id, :text, :authorId, null, :questionId, :date)\
                """;
        return session.createSQLQuery(sql)
                .setParameter("id", id)
                .setParameter("text", text)
                .setParameter("questionId", questionId)
                .setParameter("date", date)
                .setParameter("authorId", authorId);
    }
}
