package util.dao.query.builder;

import org.hibernate.Session;
import org.hibernate.query.Query;
import util.dao.query.params.AnswerQueryParameters;

import java.util.Date;

public class AnswerQueryBuilder implements SessionInitializer {

    private Session session;

    @Override
    public AnswerQueryBuilder with(Session session) {
        this.session = session;
        return this;
    }

    public void answer(Long id,
                       Boolean answered,
                       Date date,
                       String text,
                       Long questionId) {
        this.createAnswerQuery(
                id,
                1L,
                answered,
                date,
                text,
                questionId,
                session).executeUpdate();
    }

    public void answer(Long id,
                       long authorId,
                       long questionId,
                       Boolean answered,
                       Date date,
                       String text) {
        this.createAnswerQuery(
                id,
                authorId,
                answered,
                date,
                text,
                questionId,
                session).executeUpdate();
    }


    public void answer(Long id,
                       Long questionId,
                       Date date) {
        this.createAnswerQuery(
                id,
                1L,
                false,
                date,
                AnswerQueryParameters.TEXT,
                questionId,
                session).executeUpdate();
    }

    public void answer(Long id,
                       Boolean answered,
                       Date date) {
        this.createAnswerQuery(
                id,
                1L,
                answered,
                date,
                AnswerQueryParameters.TEXT,
                AnswerQueryParameters.QUESTION_ID,
                session).executeUpdate();
    }

    public void answer(Long id,
                       Boolean answered) {
        this.createAnswerQuery(
                id,
                1L,
                answered,
                AnswerQueryParameters.DATE,
                AnswerQueryParameters.TEXT,
                AnswerQueryParameters.QUESTION_ID,
                session).executeUpdate();
    }

    public void answer(Long id,
                       Date date) {
        this.createAnswerQuery(
                id,
                1L,
                AnswerQueryParameters.ANSWERED,
                date,
                AnswerQueryParameters.TEXT,
                AnswerQueryParameters.QUESTION_ID,
                session).executeUpdate();
    }

    public void answer(Long id) {
        this.createAnswerQuery(
                id,
                1L,
                AnswerQueryParameters.ANSWERED,
                AnswerQueryParameters.DATE,
                AnswerQueryParameters.TEXT,
                AnswerQueryParameters.QUESTION_ID,
                session).executeUpdate();
    }

    public void answer() {
        this.createAnswerQuery(
                1L,
                1L,
                AnswerQueryParameters.ANSWERED,
                AnswerQueryParameters.DATE,
                AnswerQueryParameters.TEXT,
                AnswerQueryParameters.QUESTION_ID,
                session).executeUpdate();
    }

    private Query<?> createAnswerQuery(Long id,
                                       long authorId,
                                       Boolean answered,
                                       Date date,
                                       String text,
                                       Long question,
                                       Session session) {
        final String sql =
                """
                INSERT INTO answer (id, answered, creation_date, text, author_id, question_id)\s\
                VALUES (:id, :answered, :date, :text, :authorId, :questionId)
                """;
        return session.createSQLQuery(sql)
                .setParameter("id", id)
                .setParameter("answered", answered)
                .setParameter("date", date)
                .setParameter("text", text)
                .setParameter("questionId", question)
                .setParameter("authorId", authorId);
    }
}
