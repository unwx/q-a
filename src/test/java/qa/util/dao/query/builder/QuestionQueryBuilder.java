package qa.util.dao.query.builder;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.util.dao.query.params.QuestionQueryParameters;

import java.util.Date;

public class QuestionQueryBuilder implements SessionInitializer {

    private Session session;
    

    @Override
    public QuestionQueryBuilder with(Session session) {
        this.session = session;
        return this;
    }

    public void question(Long id,
                         Date date,
                         String tags,
                         String text,
                         String title) {
        createQuestionQuery(
                id,
                1L,
                date,
                tags,
                text,
                title,
                session)
                .executeUpdate();
    }

    public void question(Long id,
                         long authorId,
                         Date date,
                         String tags,
                         String text,
                         String title) {
        createQuestionQuery(
                id,
                authorId,
                date,
                tags,
                text,
                title,
                session)
                .executeUpdate();
    }


    public void question(Long id,
                         Date date) {
        createQuestionQuery(
                id,
                1L,
                date,
                QuestionQueryParameters.TAGS,
                QuestionQueryParameters.TEXT,
                QuestionQueryParameters.TITLE,
                session)
                .executeUpdate();
    }

    public void question(Long id) {
        createQuestionQuery(
                id,
                1L,
                QuestionQueryParameters.DATE,
                QuestionQueryParameters.TAGS,
                QuestionQueryParameters.TEXT,
                QuestionQueryParameters.TITLE,
                session)
                .executeUpdate();
    }

    public void question() {
        createQuestionQuery(
                1L,
                1L,
                QuestionQueryParameters.DATE,
                QuestionQueryParameters.TAGS,
                QuestionQueryParameters.TEXT,
                QuestionQueryParameters.TITLE,
                session)
                .executeUpdate();
    }

    private Query<?> createQuestionQuery(Long id,
                                         long authorId,
                                         Date date,
                                         String tags,
                                         String text,
                                         String title,
                                         Session session) {
        String sql =
                """
                INSERT INTO question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                VALUES (:id, :date, :date, :tags, :text, :title, :authorId)\
                """;
        return session.createSQLQuery(sql)
                .setParameter("id", id)
                .setParameter("date", date)
                .setParameter("tags", tags)
                .setParameter("text", text)
                .setParameter("title", title)
                .setParameter("authorId", authorId);
    }
}
