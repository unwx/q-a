package qa.util.dao.query.builder;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.util.dao.query.params.CommentQueryParameters;

import java.util.Date;

public class CommentQuestionQueryBuilder implements SessionInitializer {

    private Session session;
    

    @Override
    public CommentQuestionQueryBuilder with(Session session) {
        this.session = session;
        return this;
    }

    public void commentQuestion(Long id,
                                String text,
                                Long questionId,
                                Date date) {
        createCommentQuestionQuery(
                id,
                text,
                questionId,
                date,
                session)
                .executeUpdate();
    }

    public void commentQuestion(Long id,
                                Date date) {
        createCommentQuestionQuery(
                id,
                CommentQueryParameters.TEXT,
                CommentQueryParameters.QUESTION_ID,
                date,
                session)
                .executeUpdate();
    }

    public void commentQuestion() {
        createCommentQuestionQuery(
                1L,
                CommentQueryParameters.TEXT,
                CommentQueryParameters.QUESTION_ID,
                CommentQueryParameters.DATE,
                session)
                .executeUpdate();
    }

    private Query<?> createCommentQuestionQuery(Long id,
                                                String text,
                                                Long questionId,
                                                Date date,
                                                Session session) {
        String sql =
                """
                INSERT INTO comment (comment_type, id, text, author_id, answer_id, question_id, creation_date)\s\
                VALUES ('question', :id, :text, 1, null, :questionId, :date)\
                """;
        return session.createSQLQuery(sql)
                .setParameter("id", id)
                .setParameter("text", text)
                .setParameter("questionId", questionId)
                .setParameter("date", date);
    }
}
