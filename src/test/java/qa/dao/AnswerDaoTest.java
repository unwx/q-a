package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.MockitoTest;
import qa.util.dao.AnswerDaoTestUtil;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.dao.RedisTestUtil;
import qa.util.hibernate.HibernateSessionFactoryConfigurer;
import qa.util.mock.JedisMockTestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@MockitoTest
public class AnswerDaoTest {

    private AnswerDao answerDao;
    private SessionFactory sessionFactory;
    private QuestionDaoTestUtil questionDaoTestUtil;
    private AnswerDaoTestUtil answerDaoTestUtil;
    private RedisTestUtil redisTestUtil;

    private JedisResourceCenter jedisResourceCenter;

    private final TestLogger logger = new TestLogger(AnswerDaoTest.class);

    @BeforeAll
    void init() {
        sessionFactory = HibernateSessionFactoryConfigurer.getSessionFactory();
        jedisResourceCenter = JedisMockTestUtil.mockJedisFactory();
        PropertySetterFactory propertySetterFactory = Mockito.mock(PropertySetterFactory.class);

        answerDao = new AnswerDao(propertySetterFactory, sessionFactory, jedisResourceCenter);
        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);
        redisTestUtil = new RedisTestUtil(jedisResourceCenter);
    }

    @BeforeEach
    void truncate() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table question cascade").executeUpdate();
            session.createSQLQuery("truncate table answer cascade").executeUpdate();
            session.createSQLQuery("truncate table comment cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
            transaction.commit();
        }
        JedisResource resource = jedisResourceCenter.getResource();
        resource.getJedis().flushDB();
        resource.close();
    }

    @Nested
    class get_full_answers {

        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            questionDaoTestUtil.createQuestionWithAnswersWithComments(
                    (int) (QuestionDaoTestUtil.RESULT_SIZE * 1.5),
                    QuestionDaoTestUtil.COMMENT_RESULT_SIZE);

            for (int i = 0; i < 2; i++) {
                List<Answer> answers = answerDao.getAnswers(1L, -1L, i);
                assertThat(answers, notNullValue());
                assertThat(answers.size(), greaterThan(0));
                for (Answer a : answers) {
                    assertThat(a, notNullValue());
                    assertThat(a.getId(), notNullValue());
                    assertThat(a.getText(), notNullValue());
                    assertThat(a.getAnswered(), notNullValue());
                    assertThat(a.getCreationDate(), notNullValue());
                    assertThat(a.getLikes(), equalTo(0));
                    assertThat(a.isLiked(), equalTo(false));

                    assertThat(a.getAuthor(), notNullValue());
                    assertThat(a.getAuthor().getUsername(), notNullValue());

                    assertThat(a.getComments(), notNullValue());
                    for (CommentAnswer ca : a.getComments()) {
                        assertThat(ca, notNullValue());
                        assertThat(ca.getId(), notNullValue());
                        assertThat(ca.getText(), notNullValue());
                        assertThat(ca.getCreationDate(), notNullValue());

                        assertThat(ca.getAuthor(), notNullValue());
                        assertThat(ca.getAuthor().getUsername(), notNullValue());
                    }
                }
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace("assert no duplicates");
            questionDaoTestUtil.createQuestionWithAnswersWithComments(
                    (int) (QuestionDaoTestUtil.RESULT_SIZE * 1.5),
                    QuestionDaoTestUtil.COMMENT_RESULT_SIZE);

            List<Answer> answers1 = answerDao.getAnswers(1L, -1L, 0);
            List<Answer> answers2 = answerDao.getAnswers(1L, -1L, 1);

            assertThat(answers1, notNullValue());
            assertThat(answers2, notNullValue());

            assertThat(answers1.size(), greaterThan(0));
            assertThat(answers2.size(), greaterThan(0));

            int size1 = answers1.size();
            int size2 = answers2.size();

            long[] ids1 = new long[size1];
            long[] ids2 = new long[size2];
            for (int i = 0; i < size1; i++) {
                ids1[i] = answers1.get(i).getId();
            }
            for (int i = 0; i < size2; i++) {
                ids2[i] = answers2.get(i).getId();
            }
            assertThat(ids1, equalTo(Arrays.stream(ids1).distinct().toArray()));
            assertThat(ids2, equalTo(Arrays.stream(ids2).distinct().toArray()));
        }

        @Nested
        class no_result {

            @Test
            void assert_result_equals_null_question_not_exist() {
                logger.trace("assert result equals null - when question not exist");
                assertThat(answerDao.getAnswers(1L, -1L, 1), equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list_question_exist() {
                logger.trace("assert result equals empty list - when question exist");
                questionDaoTestUtil.createQuestion();
                assertThat(answerDao.getAnswers(1L, -1L, 1), equalTo(Collections.emptyList()));
            }
        }
    }

    @Nested
    class like {
        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            answerDaoTestUtil.createAnswer();
            List<Answer> result = answerDao.getAnswers(1L, -1L, 0);
            assertThat(result, notNullValue());

            for (Answer a : result) {
                assertThat(a, notNullValue());
                assertThat(a.getLikes(), equalTo(0));
                answerDaoTestUtil.like(a.getId(), 15);
            }

            List<Answer> resultLiked = answerDao.getAnswers(1L, -1L, 0);
            assertThat(resultLiked, notNullValue());

            for (Answer a : resultLiked) {
                assertThat(a, notNullValue());
                assertThat(a.getLikes(), equalTo(15));
            }
        }

        @Test
        void assert_correct_keys() {
            logger.trace("assert correct keys");
            answerDaoTestUtil.createManyAnswers(2);
            answerDaoTestUtil.like(0L, 15);

            List<Answer> result = answerDao.getAnswers(1L, -1L, 0);
            assertThat(result, notNullValue());
            Answer answer = result.get(0);

            assertThat(answer.getLikes(), equalTo(15));
        }

        @Test
        void assert_success() {
            logger.trace("assert success");
            answerDaoTestUtil.createAnswer();
            answerDaoTestUtil.like(1L, 5);

            List<Answer> result = answerDao.getAnswers(1L, -1L, 0);
            assertThat(result, notNullValue());
            Answer answer = result.get(0);

            assertThat(result, notNullValue());
            assertThat(answer.getLikes(), equalTo(5));
        }

        @Test
        void assert_no_more_than_one() {
            logger.trace("assert can't like more than one times");
            answerDaoTestUtil.createAnswer();

            answerDao.like(1L, 1L);
            answerDao.like(1L, 1L);

            List<Answer> result = answerDao.getAnswers(1L, -1L, 0);
            assertThat(result, notNullValue());
            Answer answer = result.get(0);

            assertThat(result, notNullValue());
            assertThat(answer.getLikes(), equalTo(1));
        }

        @Test
        void assert_liked_by_user_caller() {
            logger.trace("assert get user liked status equals true");
            answerDaoTestUtil.createAnswer();
            answerDao.like(1L, 1L);

            List<Answer> result = answerDao.getAnswers(1L, 1L, 0);
            assertThat(result, notNullValue());
            Answer answer = result.get(0);

            assertThat(result, notNullValue());
            assertThat(answer.isLiked(), equalTo(true));
        }

        @Test
        void assert_not_liked_by_user_caller() {
            logger.trace("assert get user liked status equals false");
            answerDaoTestUtil.createAnswer();
            answerDao.like(-1L, 1L);

            List<Answer> result = answerDao.getAnswers(1L, 1L, 0);
            assertThat(result, notNullValue());
            Answer answer = result.get(0);

            assertThat(result, notNullValue());
            assertThat(answer.isLiked(), equalTo(false));
        }
    }

    @Nested
    class delete {
        @Test
        void assert_success() {
            logger.trace("assert success simple situation");
            answerDaoTestUtil.createAnswer();
            answerDao.like(1L, 1L);

            answerDao.delete(1L);

            final Set<String> keys = redisTestUtil.getAllKeys();
            /*
             * 3 keys created: {answer-like (id:counter), user-answer (id:id), answer-user (id:id)}
             * delete answer -> delete `answer-like` key, delete all associations with user-answer, delete answer-user key;
             * 1 key remaining
             * if user-answer is empty -> no keys remaining
             */
            assertThat(keys.size(), equalTo(0));
        }

        @Test
        void no_keys() {
            logger.trace("assert success simple situation");

            assertDoesNotThrow(() -> answerDao.delete(1L));
            final Set<String> keys = redisTestUtil.getAllKeys();
            assertThat(keys.size(), equalTo(0));
        }
    }
}
