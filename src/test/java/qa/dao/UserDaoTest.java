package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @BeforeAll
    void init() {
        PropertySetterFactory propertySetterFactory = Mockito.mock(PropertySetterFactory.class);
        userDao = new UserDao(propertySetterFactory);
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

    @Nested
    class get_full_user {
        @Test
        void assert_correct_result() {
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
            questionDaoTestUtil.createManyQuestionsWithManyAnswers(UserDaoTestUtil.RESULT_SIZE, 1);
            User user = userDao.readFullUser(UserQueryParameters.USERNAME);
            assertThat(user, notNullValue());

            List<Answer> answers = user.getAnswers();
            List<Question> questions = user.getQuestions();

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
            userDaoTestUtil.createUser();
            User u = userDao.readFullUser(UserQueryParameters.USERNAME);
            assertThat(u, notNullValue());
            assertThat(u.getQuestions(), notNullValue());
            assertThat(u.getAnswers(), notNullValue());
        }

        @Test
        void assert_not_found_result_equal_null() {
            User user = userDao.readFullUser(UserQueryParameters.USERNAME);
            assertThat(user, equalTo(null));
        }
    }

    @Nested
    class get_user_questions {

        @Test
        void assert_correct_result() {
            questionDaoTestUtil.createManyQuestions(UserDaoTestUtil.RESULT_SIZE);

            List<Question> questions = userDao.readUserQuestions(1L, 0);
            assertThat(questions, notNullValue());

            for (Question q : questions) {
                assertThat(q, notNullValue());
                assertThat(q.getId(), notNullValue());
                assertThat(q.getTitle(), notNullValue());
            }
        }

        @Test
        void assert_no_duplicates() {
            questionDaoTestUtil.createManyQuestions((int) (UserDaoTestUtil.RESULT_SIZE * 1.5));

            List<Question> questions1 = userDao.readUserQuestions(1L, 0);
            assertThat(questions1, notNullValue());

            List<Question> questions2 = userDao.readUserQuestions(1L, 1);
            assertThat(questions2, notNullValue());

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

        @Test
        void assert_not_found_result_user_not_exist_equal_empty_list() {
            List<Question> questions = userDao.readUserQuestions(1L, 0);
            assertThat(questions, equalTo(null));
        }

        @Test
        void assert_not_found_result_questions_not_exist_equal_empty_list() {
            userDaoTestUtil.createUser();
            List<Question> questions = userDao.readUserQuestions(1L, 12312);
            assertThat(questions, equalTo(Collections.emptyList()));
        }
    }

    @Nested
    class get_user_answers {
        @Test
        void assert_correct_result() {
            answersDaoTestUtil.createManyAnswers(UserDaoTestUtil.RESULT_SIZE);

            List<Answer> answers = userDao.readUserAnswers(1L, 0);
            assertThat(answers, notNullValue());

            for (Answer a : answers) {
                assertThat(a, notNullValue());
                assertThat(a.getId(), notNullValue());
                assertThat(a.getText(), notNullValue());
            }
        }

        @Test
        void assert_no_duplicates() {
            answersDaoTestUtil.createManyAnswers((int) (UserDaoTestUtil.RESULT_SIZE * 1.5));

            List<Answer> answers1 = userDao.readUserAnswers(1L, 0);
            assertThat(answers1, notNullValue());

            List<Answer> answers2 = userDao.readUserAnswers(1L, 1);
            assertThat(answers2, notNullValue());

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

        @Test
        void assert_not_found_result_user_not_exist_equal_empty_list() {
            List<Answer> answers = userDao.readUserAnswers(1L, 0);
            assertThat(answers, equalTo(null));
        }

        @Test
        void assert_not_found_result_answers_not_exist_equal_empty_list() {
            userDaoTestUtil.createUser();
            List<Answer> answers1 = userDao.readUserAnswers(1L, 0);
            assertThat(answers1, equalTo(Collections.emptyList()));
        }
    }
}
