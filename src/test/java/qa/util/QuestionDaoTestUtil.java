package qa.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Date;

public class QuestionDaoTestUtil {

    private static final long dateAtMillisDefault = 360000000000L;

    public static final int commentResultSize = 3;
    public static final int resultSize = 6;

    public static final int questionViewResultSize = 20;

    public static void createQuestionWithCommentsAndAnswersWithComments(SessionFactory sessionFactory, int answers, int comments) {
        try(Session session = sessionFactory.openSession()) {
            String createUserSql =
                    """
                    insert into usr (id, about, username) values (1, null, 'username')\
                    """;
            String createQuestionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (1, '2021-04-05', '2021-04-05', 'tags', 'text', 'title', 1)\
                    """;
            String createAnswerSql =
                    """
                    insert into answer (id, answered, creation_date, text, author_id, question_id)\s\
                    values (:id, false, :date, 'text', 1, 1)\
                    """;
            String createQuestionCommentSql =
                    """
                    insert into comment (comment_type, id, text, author_id, answer_id, creation_date, question_id)\s\
                    values ('question', :id, 'text', 1, null, :date, 1)\
                    """;
            String createAnswerCommentSql =
                    """
                    insert into comment (comment_type, id, text, author_id, answer_id, creation_date, question_id)\s\
                    values ('answer', :id, 'text', 1, :answerId, :date, null)\
                    """;
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery(createUserSql).executeUpdate();
            session.createSQLQuery(createQuestionSql).executeUpdate();

            long commentId = 0;
            for (int i = 0; i < answers; i++) {
                session.createSQLQuery(createAnswerSql)
                        .setParameter("id", (long) i)
                        .setParameter("date", new Date(i * dateAtMillisDefault))
                        .executeUpdate();
                session.createSQLQuery(createQuestionCommentSql)
                        .setParameter("id", commentId)
                        .setParameter("date", new Date(i * dateAtMillisDefault))
                        .executeUpdate();
                commentId++;
                for (int y = 0; y < comments; y++) {
                    session.createSQLQuery(createAnswerCommentSql)
                            .setParameter("answerId", i)
                            .setParameter("id", commentId)
                            .setParameter("date", new Date(y + dateAtMillisDefault))
                            .executeUpdate();
                    commentId++;
                }
                session.flush();
                session.clear();
            }
            transaction.commit();
        }
    }

    public static void createQuestionWithComments(SessionFactory sessionFactory, int comments) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String createUserSql =
                    """
                    insert into usr (id, about, username) values (1, null, 'username')\
                    """;
            String createQuestionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (1, '2021-04-05', '2021-04-05', 'tags', 'text', 'title', 1)\
                    """;
            String createQuestionCommentSql =
                    """
                    insert into comment (comment_type, id, text, author_id, answer_id, creation_date, question_id)\s\
                    values ('question', :id, 'text', 1, null, :date, 1)\
                    """;
            session.createSQLQuery(createUserSql).executeUpdate();
            session.createSQLQuery(createQuestionSql).executeUpdate();

            for (int i = 0; i < comments; i++) {
                session.createSQLQuery(createQuestionCommentSql)
                        .setParameter("id", (long) i)
                        .setParameter("date", dateAtMillisDefault * i);
                if (i % 20 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            transaction.commit();
        }
    }

    public static void createQuestionWithAnswersWithComments(SessionFactory sessionFactory, int answers, int comments) {
        try(Session session = sessionFactory.openSession()) {
            String createUserSql =
                    """
                    insert into usr (id, about, username) values (1, null, 'username')\
                    """;
            String createQuestionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (1, '2021-04-05', '2021-04-05', 'tags', 'text', 'title', 1)\
                    """;
            String createAnswerSql =
                    """
                    insert into answer (id, answered, creation_date, text, author_id, question_id)\s\
                    values (:id, false, :date, 'text', 1, 1)\
                    """;
            String createAnswerCommentSql =
                    """
                    insert into comment (comment_type, id, text, author_id, answer_id, creation_date, question_id)\s\
                    values ('answer', :id, 'text', 1, :answerId, :date, null)\
                    """;
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery(createUserSql).executeUpdate();
            session.createSQLQuery(createQuestionSql).executeUpdate();

            long commentId = 0;
            for (int i = 0; i < answers; i++) {
                session.createSQLQuery(createAnswerSql)
                        .setParameter("id", (long) i)
                        .setParameter("date", new Date(i * dateAtMillisDefault))
                        .executeUpdate();
                commentId++;
                for (int y = 0; y < comments; y++) {
                    session.createSQLQuery(createAnswerCommentSql)
                            .setParameter("answerId", i)
                            .setParameter("id", commentId)
                            .setParameter("date", new Date(y + dateAtMillisDefault + i * 1000L))
                            .executeUpdate();
                    commentId++;
                }
                session.flush();
                session.clear();
            }
            transaction.commit();
        }
    }

    public static void createManyQuestionsWithManyAnswers(SessionFactory sessionFactory, int questions, int answers) {
        try(Session session = sessionFactory.openSession()) {
            String createUserSql =
                    """
                    insert into usr (id, about, username) values (1, null, 'username')\
                    """;
            String createQuestionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (:id, :date, :date, 'tags', 'text', 'title', 1)\
                    """;
            String createAnswerSql =
                    """
                    insert into answer (id, answered, creation_date, text, author_id, question_id)\s\
                    values (:id, false, :date, 'text', 1, :questionId)\
                    """;
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery(createUserSql).executeUpdate();

            int answerId = 0;
            for (int i = 0; i < questions; i++) {
                session.createSQLQuery(createQuestionSql)
                        .setParameter("id", (long) i)
                        .setParameter("date", new Date(i * dateAtMillisDefault))
                        .executeUpdate();
                for (int y = 0; y < answers; y++) {
                    session.createSQLQuery(createAnswerSql)
                            .setParameter("id", answerId)
                            .setParameter("questionId", i)
                            .setParameter("date", new Date(y + dateAtMillisDefault + i * 1000L))
                            .executeUpdate();
                    answerId++;
                }
                session.flush();
                session.clear();
            }
            transaction.commit();
        }
    }
}
