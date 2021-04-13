package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import qa.dao.query.UserQueryFactory;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.Logged;
import qa.tools.annotations.MockitoTest;
import qa.util.dao.AnswerDaoTestUtil;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.dao.UserDaoTestUtil;
import qa.util.dao.query.params.UserQueryParameters;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@MockitoTest
public class UserDaoTest {

    private UserDao userDao;
    private SessionFactory sessionFactory;
    private QuestionDaoTestUtil questionDaoTestUtil;
    private AnswerDaoTestUtil answersDaoTestUtil;
    private UserDaoTestUtil userDaoTestUtil;

    private final TestLogger logger = new TestLogger(UserDaoTest.class);

    @BeforeAll
    void init() {
        PropertySetterFactory propertySetterFactory = Mockito.mock(PropertySetterFactory.class);
        UserQueryFactory userQueryFactory = Mockito.spy(new UserQueryFactory());

        userDao = new UserDao(propertySetterFactory, userQueryFactory);
        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory);
        answersDaoTestUtil = new AnswerDaoTestUtil(sessionFactory);
        userDaoTestUtil = new UserDaoTestUtil(sessionFactory);
    }

    @BeforeEach
    void truncate() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table question cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
            transaction.commit();
        }
    }

    @Logged
    class get_full_user {

        @BeforeAll
        void init() {
            logger.nested(get_full_user.class);
        }

        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            questionDaoTestUtil.createManyQuestionsWithManyAnswers(UserDaoTestUtil.RESULT_SIZE, 1);
            User user = userDao.readFullUser(UserQueryParameters.USERNAME);
            assertThat(user, notNullValue());

            assertThat(user.getAnswers().size(), greaterThan(0));
            assertThat(user.getQuestions().size(), greaterThan(0));

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
        void assert_no_duplicates() {

            logger.trace("assert no duplicates");
            questionDaoTestUtil.createManyQuestionsWithManyAnswers(UserDaoTestUtil.RESULT_SIZE, 1);
            User user = userDao.readFullUser(UserQueryParameters.USERNAME);
            assertThat(user, notNullValue());

            List<Answer> answers = user.getAnswers();
            List<Question> questions = user.getQuestions();

            assertThat(answers.size(), greaterThan(0));
            assertThat(questions.size(), greaterThan(0));

            int answersSize = answers.size();
            int questionsSize = questions.size();

            long[] answersId = new long[answersSize];
            long[] questionsId = new long[questionsSize];

            for (int i = 0; i < answersSize; i++) {
                answersId[i] = answers.get(i).getId();
            }

            for (int i = 0; i < questionsSize; i++) {
                questionsId[i] = questions.get(i).getId();
            }

            assertThat(answersId, equalTo(Arrays.stream(answersId).distinct().toArray()));
            assertThat(questionsId, equalTo(Arrays.stream(questionsId).distinct().toArray()));
        }

        @Test
        void assert_no_null_pointer_exception_user_created_only() {
            logger.trace("assert no NPE when user created only");
            userDaoTestUtil.createUser();
            User u = userDao.readFullUser(UserQueryParameters.USERNAME);
            assertThat(u, notNullValue());
            assertThat(u.getQuestions(), notNullValue());
            assertThat(u.getAnswers(), notNullValue());
        }

        @Test
        void assert_not_found_result_equals_null() {
            logger.trace("assert not found result equals null");
            User user = userDao.readFullUser(UserQueryParameters.USERNAME);
            assertThat(user, equalTo(null));
        }
    }

    @Logged
    class get_user_questions {

        @BeforeAll
        void init() {
            logger.nested(get_user_questions.class);
        }

        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            questionDaoTestUtil.createManyQuestions(UserDaoTestUtil.RESULT_SIZE);

            List<Question> questions = userDao.readUserQuestions(1L, 0);
            assertThat(questions, notNullValue());
            assertThat(questions.size(), greaterThan(0));

            for (Question q : questions) {
                assertThat(q, notNullValue());
                assertThat(q.getId(), notNullValue());
                assertThat(q.getTitle(), notNullValue());
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace("assert no duplicates");
            questionDaoTestUtil.createManyQuestions((int) (UserDaoTestUtil.RESULT_SIZE * 1.5));

            List<Question> questions1 = userDao.readUserQuestions(1L, 0);
            List<Question> questions2 = userDao.readUserQuestions(1L, 1);

            assertThat(questions1, notNullValue());
            assertThat(questions2, notNullValue());

            assertThat(questions1.size(), greaterThan(0));
            assertThat(questions2.size(), greaterThan(0));

            int questions1Size = questions1.size();
            int questions2Size = questions2.size();

            long[] ids1 = new long[questions1Size];
            long[] ids2 = new long[questions2Size];

            for (int i = 0; i < questions1Size; i++) {
                ids1[i] = questions1.get(i).getId();
            }

            for (int i = 0; i < questions2Size; i++) {
                ids2[i] = questions2.get(i).getId();
            }

            assertThat(ids1, equalTo(Arrays.stream(ids1).distinct().toArray()));
            assertThat(ids2, equalTo(Arrays.stream(ids2).distinct().toArray()));
        }

        @Logged
        class not_found {

            @BeforeAll
            void init() {
                logger.nested(not_found.class);
            }

            @Test
            void assert_result_equals_null_user_not_exist() {
                logger.trace("assert result equals null when user not exist");
                List<Question> questions = userDao.readUserQuestions(1L, 0);
                assertThat(questions, equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list() {
                logger.trace("assert result equals empty list when user exist");
                userDaoTestUtil.createUser();
                List<Question> questions = userDao.readUserQuestions(1L, 12312);
                assertThat(questions, equalTo(Collections.emptyList()));
            }
        }
    }

    @Logged
    class get_user_answers {

        @BeforeAll
        void init() {
            logger.nested(get_user_answers.class);
        }

        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            answersDaoTestUtil.createManyAnswers(UserDaoTestUtil.RESULT_SIZE);

            List<Answer> answers = userDao.readUserAnswers(1L, 0);
            assertThat(answers, notNullValue());
            assertThat(answers.size(), greaterThan(0));

            for (Answer a : answers) {
                assertThat(a, notNullValue());
                assertThat(a.getId(), notNullValue());
                assertThat(a.getText(), notNullValue());
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace("assert no duplicates");
            answersDaoTestUtil.createManyAnswers((int) (UserDaoTestUtil.RESULT_SIZE * 1.5));

            List<Answer> answers1 = userDao.readUserAnswers(1L, 0);
            List<Answer> answers2 = userDao.readUserAnswers(1L, 1);

            assertThat(answers1, notNullValue());
            assertThat(answers2, notNullValue());

            assertThat(answers1.size(), greaterThan(0));
            assertThat(answers2.size(), greaterThan(0));

            int answer1Size = answers1.size();
            int answer2Size = answers2.size();

            long[] ids1 = new long[answer1Size];
            long[] ids2 = new long[answer2Size];

            for (int i = 0; i < answer1Size; i++) {
                ids1[i] = answers1.get(i).getId();
            }

            for (int i = 0; i < answer2Size; i++) {
                ids2[i] = answers2.get(i).getId();
            }

            assertThat(ids1, equalTo(Arrays.stream(ids1).distinct().toArray()));
            assertThat(ids2, equalTo(Arrays.stream(ids2).distinct().toArray()));
        }

        @Logged
        class not_found {

            @BeforeAll
            void init() {
                logger.nested(not_found.class);
            }

            @Test
            void assert_result_equals_null_user_not_exist() {
                logger.trace("assert result equals null when user not exist");
                List<Answer> answers = userDao.readUserAnswers(1L, 0);
                assertThat(answers, equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list_user_exist() {
                logger.trace("assert result equals empty list when user exist");
                userDaoTestUtil.createUser();
                List<Answer> answers1 = userDao.readUserAnswers(1L, 0);
                assertThat(answers1, equalTo(Collections.emptyList()));
            }
        }
    }

    @AfterAll
    void close() {
        logger.end();
    }
}
