package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import qa.cache.JedisResourceCenter;
import qa.dao.util.HibernateSessionFactoryConfigurer;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.MockitoTest;
import util.dao.AnswerDaoTestUtil;
import util.dao.QuestionDaoTestUtil;
import util.dao.TruncateUtil;
import util.dao.UserDaoTestUtil;
import util.dao.query.params.UserQueryParameters;
import util.mock.MockUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@MockitoTest
public class UserDaoTest {

    private UserDao userDao;
    private QuestionDaoTestUtil questionDaoTestUtil;
    private AnswerDaoTestUtil answersDaoTestUtil;
    private UserDaoTestUtil userDaoTestUtil;

    private SessionFactory sessionFactory;

    private static final String LOG_CORRECT_RESULT          = "assert correct result";
    private static final String LOG_NO_DUPLICATES           = "assert no duplicates";
    private static final String LOG_NO_NPE_USER             = "assert no NPE when user created only";
    private static final String LOG_RESULT_NULL             = "assert not found result equals null";
    private static final String LOG_RESULT_NULL_USER        = "assert result equals null when user not exist";
    private static final String LOG_RESULT_EMPTY_LIST       = "assert result equals empty list when user exist";

    private final TestLogger logger = new TestLogger(UserDaoTest.class);

    @BeforeAll
    void init() {
        sessionFactory = HibernateSessionFactoryConfigurer.getSessionFactory();
        JedisResourceCenter jedisResourceCenter = MockUtil.mockJedisCenter();
        PropertySetterFactory propertySetterFactory = Mockito.spy(PropertySetterFactory.class);

        userDao = new UserDao(propertySetterFactory, sessionFactory);
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);
        answersDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
        userDaoTestUtil = new UserDaoTestUtil(sessionFactory);
    }

    @BeforeEach
    void truncate() {
        try (Session session = sessionFactory.openSession()) {
            TruncateUtil.truncatePQ(session);
        }
    }

    @Nested
    class get_full_user {

        @Test
        void assert_correct_result() {
            logger.trace(LOG_CORRECT_RESULT);

            final int answers = 1;
            questionDaoTestUtil.createManyQuestionsWithManyAnswers(UserDaoTestUtil.RESULT_SIZE, answers);

            final User user = userDao.readFullUser(UserQueryParameters.USERNAME);
            assertThat(user, notNullValue());

            assertThat(user.getAnswers().isEmpty(), equalTo(false));
            assertThat(user.getQuestions().isEmpty(), equalTo(false));

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
            logger.trace(LOG_NO_DUPLICATES);

            final int answersCount = 1;
            questionDaoTestUtil.createManyQuestionsWithManyAnswers(UserDaoTestUtil.RESULT_SIZE, answersCount);

            final User user = userDao.readFullUser(UserQueryParameters.USERNAME);
            assertThat(user, notNullValue());

            final List<Answer> answers = user.getAnswers();
            final List<Question> questions = user.getQuestions();

            assertThat(answers.isEmpty(), equalTo(false));
            assertThat(questions.isEmpty(), equalTo(false));

            final int answersSize = answers.size();
            final int questionsSize = questions.size();

            final long[] answersId = new long[answersSize];
            final long[] questionsId = new long[questionsSize];

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
            logger.trace(LOG_NO_NPE_USER);
            userDaoTestUtil.createUser();

            final User u = userDao.readFullUser(UserQueryParameters.USERNAME);

            assertThat(u, notNullValue());
            assertThat(u.getQuestions(), notNullValue());
            assertThat(u.getAnswers(), notNullValue());
        }

        @Test
        void assert_not_found_result_equals_null() {
            logger.trace(LOG_RESULT_NULL);
            final User user = userDao.readFullUser(UserQueryParameters.USERNAME);
            assertThat(user, equalTo(null));
        }
    }

    @Nested
    class get_user_questions {

        @Test
        void assert_correct_result() {
            logger.trace(LOG_CORRECT_RESULT);
            questionDaoTestUtil.createManyQuestions(UserDaoTestUtil.RESULT_SIZE);

            final long userId = 1L;
            final int page = 0;

            final List<Question> questions = userDao.readUserQuestions(userId, page);
            assertThat(questions, notNullValue());
            assertThat(questions.isEmpty(), equalTo(false));

            for (Question q : questions) {
                assertThat(q, notNullValue());
                assertThat(q.getId(), notNullValue());
                assertThat(q.getTitle(), notNullValue());
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace(LOG_NO_DUPLICATES);
            questionDaoTestUtil.createManyQuestions((int) (UserDaoTestUtil.RESULT_SIZE * 1.5));

            final long userId = 1L;
            int page = 0;

            final List<Question> questions1 = userDao.readUserQuestions(userId, page);
            final List<Question> questions2 = userDao.readUserQuestions(userId, ++page);

            assertThat(questions1, notNullValue());
            assertThat(questions2, notNullValue());

            assertThat(questions1.isEmpty(), equalTo(false));
            assertThat(questions2.isEmpty(), equalTo(false));

            final int questions1Size = questions1.size();
            final int questions2Size = questions2.size();

            final long[] ids1 = new long[questions1Size];
            final long[] ids2 = new long[questions2Size];

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
            void assert_result_equals_null_user_not_exist() {
                logger.trace(LOG_RESULT_NULL_USER);

                final long userId = 1L;
                final int page = 0;

                final List<Question> questions = userDao.readUserQuestions(userId, page);
                assertThat(questions, equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list() {
                logger.trace(LOG_RESULT_EMPTY_LIST);
                userDaoTestUtil.createUser();

                final long userId = 1L;
                final int page = 0;

                final List<Question> questions = userDao.readUserQuestions(userId, page);
                assertThat(questions, equalTo(Collections.emptyList()));
            }
        }
    }

    @Nested
    class get_user_answers {

        @Test
        void assert_correct_result() {
            logger.trace(LOG_CORRECT_RESULT);
            answersDaoTestUtil.createManyAnswers(UserDaoTestUtil.RESULT_SIZE);

            final long userId = 1L;
            final int page = 0;

            final List<Answer> answers = userDao.readUserAnswers(userId, page);
            assertThat(answers, notNullValue());
            assertThat(answers.isEmpty(), equalTo(false));

            for (Answer a : answers) {
                assertThat(a, notNullValue());
                assertThat(a.getId(), notNullValue());
                assertThat(a.getText(), notNullValue());
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace(LOG_NO_DUPLICATES);
            answersDaoTestUtil.createManyAnswers((int) (UserDaoTestUtil.RESULT_SIZE * 1.5));

            final long userId = 1L;
            int page = 0;

            final List<Answer> answers1 = userDao.readUserAnswers(userId, page);
            final List<Answer> answers2 = userDao.readUserAnswers(userId, page);

            assertThat(answers1, notNullValue());
            assertThat(answers2, notNullValue());

            assertThat(answers1.isEmpty(), equalTo(false));
            assertThat(answers2.size(), greaterThan(0));

            final int answer1Size = answers1.size();
            final int answer2Size = answers2.size();

            final long[] ids1 = new long[answer1Size];
            final long[] ids2 = new long[answer2Size];

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
            void assert_result_equals_null_user_not_exist() {
                logger.trace(LOG_RESULT_NULL_USER);

                final long userId = 1L;
                final int page = 0;

                final List<Answer> answers = userDao.readUserAnswers(userId, page);
                assertThat(answers, equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list_user_exist() {
                logger.trace(LOG_RESULT_EMPTY_LIST);
                userDaoTestUtil.createUser();

                final long userId = 1L;
                final int page = 0;

                final List<Answer> answers1 = userDao.readUserAnswers(userId, page);
                assertThat(answers1, equalTo(Collections.emptyList()));
            }
        }
    }
}
