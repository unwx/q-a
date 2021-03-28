package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import qa.config.spring.SpringConfig;
import qa.domain.Question;
import qa.domain.User;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    private final SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final static String username = "username";

    @BeforeEach
    void truncate() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table question cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void readFullUserNotFound() {
        User user = userDao.readFullUser(username);
        assertThat(user, equalTo(null));
    }

    @Test
    void readFullUser() {
        createUserWithQuestionsAndAnswers();
        User user = userDao.readFullUser(username);
        assertThat(user, notNullValue());

        assertThat(user.getAnswers().size(), greaterThan(0));
        assertThat(user.getQuestions().size(), greaterThan(0));

        assertThat(user.getAnswers().get(0).getText(), equalTo("text"));
        assertThat(user.getAnswers().get(0).getAnswered(), equalTo(null));

        assertThat(user.getUsername(), notNullValue());
        assertThat(user.getId(), notNullValue());
    }

    @Test
    void readUserQuestions_NotFound() {
        List<Question> questions = userDao.readUserQuestions(1L, 0);
        assertThat(questions, equalTo(null));

        createUser();
        List<Question> questions1 = userDao.readUserQuestions(1L, 0);
        assertThat(questions1, equalTo(null));
    }

    @Test
    void readUserQuestions_IncorrectPage() {
        List<Question> questions = userDao.readUserQuestions(1L, 12312);
        assertThat(questions, equalTo(null));

        createUser();
        List<Question> questions1 = userDao.readUserQuestions(1L, 12312);
        assertThat(questions1, equalTo(null));
    }

    @Test
    void readUserQuestions_AssertNoDuplicates() throws NoSuchFieldException, IllegalAccessException {
        Field resultSizeField = UserDao.class.getDeclaredField("resultSize");
        resultSizeField.setAccessible(true);
        int resultSize = (int) resultSizeField.get(userDao);

        createUserWithManyQuestions();
        List<Question> questions = userDao.readUserQuestions(1L, 0);

        assertThat(questions, notNullValue());
        assertThat(questions.size(), equalTo(resultSize));

        List<Question> questions1 = userDao.readUserQuestions(1L, 1);
        assertThat(questions1, notNullValue());
        assertThat(questions1.size(), equalTo(resultSize));

        List<Question> all = new LinkedList<>(questions);
        all.addAll(questions1);
        List<Question> filtered = all.stream().distinct().collect(Collectors.toList());
        assertThat(filtered.size(), equalTo(resultSize * 2));
    }

    private void createUserWithQuestionsAndAnswers() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, 'about', '%s')
                    """.formatted(username);
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

            for (long i = 0; i < 3; i++) {
                session.createSQLQuery(questionSql).setParameter("id", i).executeUpdate();
            }
            for (long i = 0; i < 2; i++) {
                session.createSQLQuery(answerSql).setParameter("id", i).executeUpdate();
            }
            transaction.commit();
        }
    }

    private void createUser() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, 'about', '%s')
                    """.formatted(username);
            session.createSQLQuery(userSql).executeUpdate();
            transaction.commit();
        }
    }

    private void createUserWithManyQuestions() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, 'about', '%s')
                    """.formatted(username);
            String questionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (:id, '%s', '%s', 'tag, tag', :text, 'title', 1)
                    """.formatted(new Date(), new Date());

            session.createSQLQuery(userSql).executeUpdate();
            for (long i = 0; i < 50; i++) {
                session.createSQLQuery(questionSql)
                        .setParameter("id", i)
                        .setParameter("text", String.valueOf(i))
                        .executeUpdate();
            }
            transaction.commit();
        }
    }
}
