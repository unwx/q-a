package qa.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Date;

public class UserDaoTestUtil {

    public static final String USERNAME = "username";
    public static final int RESULT_SIZE = 10;

    private UserDaoTestUtil() {
    }

    public static void createUserWithQuestionsAndAnswers(SessionFactory sessionFactory, int questions, int answers) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, 'about', '%s')
                    """.formatted(USERNAME);
            String questionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (:id, '%s', '%s', 'tag, tag', 'text', 'title', 1)
                    """.formatted(new Date(), new Date());
            String answerSql =
                    """
                    insert into answer (id, answered, creation_date, text, author_id, question_id)\s\
                    values (:id, false, '%s', 'text', 1, :id)
                    """.formatted(new Date());
            session.createSQLQuery(userSql).executeUpdate();

            int answerIndex = 0;
            for (long i = 0; i < questions; i++) {
                session.createSQLQuery(questionSql).setParameter("id", i).executeUpdate();
                for (long y = 0; y < answers; y++) {
                    session.createSQLQuery(answerSql).setParameter("id", answerIndex).executeUpdate();
                    answerIndex++;
                }
                session.flush();
                session.clear();
            }

            transaction.commit();
        }
    }

    public static void createUser(SessionFactory sessionFactory) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, 'about', '%s')
                    """.formatted(USERNAME);
            session.createSQLQuery(userSql).executeUpdate();
            transaction.commit();
        }
    }

    public static void createUserWithManyQuestions(SessionFactory sessionFactory, int questions) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, 'about', '%s')
                    """.formatted(USERNAME);
            String questionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (:id, '%s', '%s', 'tag, tag', 'text', :title, 1)
                    """.formatted(new Date(), new Date());

            session.createSQLQuery(userSql).executeUpdate();
            for (long i = 0; i < questions; i++) {
                session.createSQLQuery(questionSql)
                        .setParameter("id", i)
                        .setParameter("title", String.valueOf(i))
                        .executeUpdate();
                if (i % 20 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            transaction.commit();
        }
    }

    public static void createUserWithManyAnswers(SessionFactory sessionFactory, int answers) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, 'about', '%s')
                    """.formatted(USERNAME);
            String questionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (1, '%s', '%s', 'tag, tag', 'text', 'title', 1)
                    """.formatted(new Date(), new Date());
            String answerSql =
                    """
                    insert into answer (id, answered, creation_date, text, author_id, question_id)\s\
                    values (:id, false, '%s', :text, 1, 1)
                    """.formatted(new Date());

            session.createSQLQuery(userSql).executeUpdate();
            session.createSQLQuery(questionSql).executeUpdate();
            for (long i = 0; i < answers; i++) {
                session.createSQLQuery(answerSql)
                        .setParameter("id", i)
                        .setParameter("text", String.valueOf(i))
                        .executeUpdate();
                if (i % 20 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            transaction.commit();
        }
    }
}
