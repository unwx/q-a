package qa.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.TestLogger;
import qa.dao.query.UserQueryFactory;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
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

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class UserDaoTest {

    private UserDao userDao;
    private SessionFactory sessionFactory;
    private QuestionDaoTestUtil questionDaoTestUtil;
    private AnswerDaoTestUtil answersDaoTestUtil;
    private UserDaoTestUtil userDaoTestUtil;

    private static final Logger logger = LogManager.getLogger(UserDaoTest.class);

    @BeforeAll
    void init() {
        TestLogger.info(logger, "init", 3);
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
            TestLogger.info(logger, "truncate", 3);
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table question cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
            transaction.commit();
        }
    }

    @Nested
    class get_full_user {
        @Test
        void assert_correct_result() {
            TestLogger.trace(logger, "get full user -> assert correct result", 3);
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
            TestLogger.trace(logger, "get full user -> assert no duplicates", 3);
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
            TestLogger.trace(logger, "get full user -> assert no NPE user created only", 3);
            userDaoTestUtil.createUser();
            User u = userDao.readFullUser(UserQueryParameters.USERNAME);
            assertThat(u, notNullValue());
            assertThat(u.getQuestions(), notNullValue());
            assertThat(u.getAnswers(), notNullValue());
        }

        @Test
        void assert_not_found_result_equal_null() {
            TestLogger.trace(logger, "get full user -> assert not found result equals null", 3);
            User user = userDao.readFullUser(UserQueryParameters.USERNAME);
            assertThat(user, equalTo(null));
        }
    }

    @Nested
    class get_user_questions {

        @Test
        void assert_correct_result() {
            TestLogger.trace(logger, "get user questions -> assert correct result", 3);
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
            TestLogger.trace(logger, "get user questions -> assert no duplicates", 3);
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

        @Nested
        class not_found {
            @Test
            void assert_result_user_not_exist_equal_null() {
                TestLogger.trace(logger, "get user questions -> not found -> assert result equals null if user not exist", 3);
                List<Question> questions = userDao.readUserQuestions(1L, 0);
                assertThat(questions, equalTo(null));
            }

            @Test
            void assert_result_equal_empty_list() {
                TestLogger.trace(logger, "get user questions -> not found -> assert result equals empty list if user exist", 3);
                userDaoTestUtil.createUser();
                List<Question> questions = userDao.readUserQuestions(1L, 12312);
                assertThat(questions, equalTo(Collections.emptyList()));
            }
        }
    }

    @Nested
    class get_user_answers {
        @Test
        void assert_correct_result() {
            TestLogger.trace(logger, "get user answers -> assert correct result", 3);
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
            TestLogger.trace(logger, "get user answers -> assert no duplicates", 3);
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

        @Nested
        class not_found {
            @Test
            void assert_user_not_exist_equal_null() {
                TestLogger.trace(logger, "get user answers -> not found -> assert user not exist equal null", 3);
                List<Answer> answers = userDao.readUserAnswers(1L, 0);
                assertThat(answers, equalTo(null));
            }

            @Test
            void assert_answers_not_exist_equal_empty_list() {
                TestLogger.trace(logger, "get user answers -> not found -> assert user exist equal empty list", 3);
                userDaoTestUtil.createUser();
                List<Answer> answers1 = userDao.readUserAnswers(1L, 0);
                assertThat(answers1, equalTo(Collections.emptyList()));
            }
        }
    }
}
