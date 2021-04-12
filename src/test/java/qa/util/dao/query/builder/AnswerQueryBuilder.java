package qa.util.dao.query.builder;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.util.dao.query.params.AnswerQueryParameters;

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
        createAnswerQuery(
                id,
                answered,
                date,
                text,
                questionId,
                session).executeUpdate();
    }

    public void answer(Long id,
                       Long questionId,
                       Date date) {
        createAnswerQuery(
                id,
                false,
                date,
                AnswerQueryParameters.TEXT,
                questionId,
                session).executeUpdate();
    }

    public void answer(Long id,
                       Boolean answered,
                       Date date) {
        createAnswerQuery(
                id,
                answered,
                date,
                AnswerQueryParameters.TEXT,
                AnswerQueryParameters.QUESTION_ID,
                session).executeUpdate();
    }

    public void answer(Long id,
                       Boolean answered) {
        createAnswerQuery(
                id,
                answered,
                AnswerQueryParameters.DATE,
                AnswerQueryParameters.TEXT,
                AnswerQueryParameters.QUESTION_ID,
                session).executeUpdate();
    }

    public void answer(Long id,
                       Date date) {
        createAnswerQuery(
                id,
                AnswerQueryParameters.ANSWERED,
                date,
                AnswerQueryParameters.TEXT,
                AnswerQueryParameters.QUESTION_ID,
                session).executeUpdate();
    }

    public void answer(Long id) {
        createAnswerQuery(
                id,
                AnswerQueryParameters.ANSWERED,
                AnswerQueryParameters.DATE,
                AnswerQueryParameters.TEXT,
                AnswerQueryParameters.QUESTION_ID,
                session).executeUpdate();
    }

    public void answer() {
        createAnswerQuery(
                1L,
                AnswerQueryParameters.ANSWERED,
                AnswerQueryParameters.DATE,
                AnswerQueryParameters.TEXT,
                AnswerQueryParameters.QUESTION_ID,
                session).executeUpdate();
    }

    private Query<?> createAnswerQuery(Long id,
                                       Boolean answered,
                                       Date date,
                                       String text,
                                       Long question,
                                       Session session) {
        String sql =
                """
                INSERT INTO answer (id, answered, creation_date, text, author_id, question_id)\s\
                VALUES (:id, :answered, :date, :text, 1, :questionId)
                """;
        return session.createSQLQuery(sql)
                .setParameter("id", id)
                .setParameter("answered", answered)
                .setParameter("date", date)
                .setParameter("text", text)
                .setParameter("questionId", question);
    }
}
