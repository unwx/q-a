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
import qa.domain.Answer;
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
    void readFullUser_AssertCorrectData() throws NoSuchFieldException, IllegalAccessException {
        Field resultSizeField = UserDao.class.getDeclaredField("resultSize");
        resultSizeField.setAccessible(true);
        int resultSize = (int) resultSizeField.get(userDao);

        createUserWithQuestionsAndAnswers();
        User user = userDao.readFullUser(username);
        assertThat(user, notNullValue());

        assertThat(user.getAnswers().size(), lessThan(resultSize + 1));
        assertThat(user.getQuestions().size(), lessThan(resultSize + 1));

        assertThat(user.getUsername(), notNullValue());
        assertThat(user.getId(), notNullValue());
        assertThat(user.getAbout(), notNullValue());

        for (Question q : user.getQuestions()) {
            assertThat(q.getId(), notNullValue());
            assertThat(q.getTitle(), notNullValue());
        }

        for (Answer a : user.getAnswers()) {
            assertThat(a.getId(), notNullValue());
            assertThat(a.getText(), notNullValue());
        }

        assertThat(user.getUsername(), notNullValue());
        assertThat(user.getId(), notNullValue());
    }

    @Test
    void readFullUser_AssertNoNPE() {
        createUser();
        User u = userDao.readFullUser(username);
        assertThat(u, notNullValue());
        assertThat(u.getQuestions(), notNullValue());
        assertThat(u.getAnswers(), notNullValue());
    }

    @Test
    void readFullUser_AssertNoNPE1() {
        User u = userDao.readFullUser(username);
        assertThat(u, equalTo(null));
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
        createUserWithManyQuestions();
        List<Question> questions = userDao.readUserQuestions(1L, 12312);
        assertThat(questions, equalTo(null));
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

    @Test
    void readUserQuestions_AssertCorrectData() throws NoSuchFieldException, IllegalAccessException {
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

        for (int i = 0; i < resultSize; i++) {
            assertThat(questions.get(i).getId(), notNullValue());
            assertThat(questions.get(i).getTitle(), notNullValue());
        }

        for (int i = 0; i < resultSize; i++) {
            assertThat(questions1.get(i).getId(), notNullValue());
            assertThat(questions1.get(i).getTitle(), notNullValue());
        }
    }

    @Test
    void readUserAnswers_NotFound() {
        List<Answer> answers = userDao.readUserAnswers(1L, 0);
        assertThat(answers, equalTo(null));

        createUser();
        List<Answer> answers1 = userDao.readUserAnswers(1L, 0);
        assertThat(answers1, equalTo(null));
    }

    @Test
    void readUserAnswers_IncorrectPage() {
        createUserWithManyAnswers();
        List<Answer> answers = userDao.readUserAnswers(1L, 121233);
        assertThat(answers, equalTo(null));
    }

    @Test
    void readUserAnswers_AssertNoDuplicates() throws NoSuchFieldException, IllegalAccessException {
        Field resultSizeField = UserDao.class.getDeclaredField("resultSize");
        resultSizeField.setAccessible(true);
        int resultSize = (int) resultSizeField.get(userDao);

        createUserWithManyAnswers();

        List<Answer> answers = userDao.readUserAnswers(1L, 0);
        assertThat(answers, notNullValue());
        assertThat(answers.size(), equalTo(resultSize));

        List<Answer> answers1 = userDao.readUserAnswers(1L, 1);
        assertThat(answers1, notNullValue());
        assertThat(answers1.size(), equalTo(resultSize));

        List<Answer> all = new LinkedList<>(answers);
        all.addAll(answers1);
        List<Answer> filtered = all.stream().distinct().collect(Collectors.toList());
        assertThat(filtered.size(), equalTo(resultSize * 2));
    }

    @Test
    void readUserAnswers_AssertCorrectData() throws NoSuchFieldException, IllegalAccessException {
        Field resultSizeField = UserDao.class.getDeclaredField("resultSize");
        resultSizeField.setAccessible(true);
        int resultSize = (int) resultSizeField.get(userDao);

        createUserWithManyAnswers();

        List<Answer> answers = userDao.readUserAnswers(1L, 0);
        assertThat(answers, notNullValue());
        assertThat(answers.size(), equalTo(resultSize));

        List<Answer> answers1 = userDao.readUserAnswers(1L, 1);
        assertThat(answers1, notNullValue());
        assertThat(answers1.size(), equalTo(resultSize));

        for (int i = 0; i < resultSize; i++) {
            assertThat(answers.get(i).getId(), notNullValue());
            assertThat(answers.get(i).getText(), notNullValue());
        }

        for (int i = 0; i < resultSize; i++) {
            assertThat(answers1.get(i).getId(), notNullValue());
            assertThat(answers1.get(i).getText(), notNullValue());
        }
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
                    values (:id, '%s', '%s', 'tag, tag', 'text', :title, 1)
                    """.formatted(new Date(), new Date());

            session.createSQLQuery(userSql).executeUpdate();
            for (long i = 0; i < 50; i++) {
                session.createSQLQuery(questionSql)
                        .setParameter("id", i)
                        .setParameter("title", String.valueOf(i))
                        .executeUpdate();
            }
            transaction.commit();
        }
    }

    private void createUserWithManyAnswers() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, 'about', '%s')
                    """.formatted(username);
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
            for (long i = 0; i < 50; i++) {
                session.createSQLQuery(answerSql)
                        .setParameter("id", i)
                        .setParameter("text", String.valueOf(i))
                        .executeUpdate();
            }
            transaction.commit();
        }
    }
}
