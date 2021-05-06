package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.cache.like.AnswerLikeProvider;
import qa.dao.util.HibernateSessionFactoryConfigurer;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.MockitoTest;
import redis.clients.jedis.Jedis;
import util.dao.AnswerDaoTestUtil;
import util.dao.QuestionDaoTestUtil;
import util.dao.RedisTestUtil;
import util.dao.TruncateUtil;
import util.mock.MockUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@MockitoTest
public class AnswerDaoTest {

    private AnswerDao answerDao;

    private QuestionDaoTestUtil questionDaoTestUtil;
    private AnswerDaoTestUtil answerDaoTestUtil;
    private RedisTestUtil redisTestUtil;

    private SessionFactory sessionFactory;
    private JedisResourceCenter jedisResourceCenter;

    private static final String LOG_CORRECT_RESULT          = "assert correct result";
    private static final String LOG_NO_DUPLICATES           = "assert no duplicates";
    private static final String LOG_RESULT_NULL             = "assert result equals null, when question not exist";
    private static final String LOG_RESULT_EMPTY_LIST       = "assert result equals empty list - when question exist";
    private static final String LOG_CORRECT_KEYS            = "assert result equals empty list - when question exist";
    private static final String LOG_LIKE_ONE_TIME           = "assert can't like more than one times";
    private static final String LOG_LIKED_STATUS_TRUE       = "assert get user liked status equals true";
    private static final String LOG_LIKED_STATUS_FALSE      = "assert get user liked status equals false";
    private static final String LOG_REMOVES_LINKED_KEYS     = "assert removes all linked keys";
    private static final String LOG_REMOVES_NESTED_KEYS     = "assert removes all linked keys";
    private static final String LOG_NO_EXCEPTIONS           = "assert removes all linked keys";

    private final TestLogger logger = new TestLogger(AnswerDaoTest.class);

    @BeforeAll
    void init() {
        final PropertySetterFactory propertySetterFactory = Mockito.spy(PropertySetterFactory.class);
        final AnswerLikeProvider likesProvider = MockUtil.mockAnswerLikeProvider();
        this.sessionFactory = HibernateSessionFactoryConfigurer.getSessionFactory();
        this.jedisResourceCenter = MockUtil.mockJedisCenter();


        this.answerDao = new AnswerDao(propertySetterFactory, sessionFactory, jedisResourceCenter, likesProvider);
        this.answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
        this.questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);
        this.redisTestUtil = new RedisTestUtil(jedisResourceCenter);
    }

    @BeforeEach
    void truncate() {
        try (Session session = sessionFactory.openSession()) {
            TruncateUtil.truncatePQ(session);
        }
        try(JedisResource resource = jedisResourceCenter.getResource()) {
            final Jedis jedis = resource.getJedis();
            TruncateUtil.truncateRedis(jedis);
        }
    }

    @Nested
    class get_full_answers {

        @Test
        void assert_correct_result() {
            logger.trace(LOG_CORRECT_RESULT);
            questionDaoTestUtil.createQuestionWithAnswersWithComments(
                    (int) (QuestionDaoTestUtil.RESULT_SIZE * 1.5),
                    QuestionDaoTestUtil.COMMENT_RESULT_SIZE);

            final long questionId = 1L;
            final long userId = -1L;
            final int page = 0;

            final List<Answer> answers = answerDao.getAnswers(questionId, userId, page);

            assertThat(answers, notNullValue());
            assertThat(answers.isEmpty(), equalTo(false));

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

                    assertThat(ca.getLikes(), equalTo(0));
                    assertThat(ca.isLiked(), equalTo(false));

                    assertThat(ca.getAuthor(), notNullValue());
                    assertThat(ca.getAuthor().getUsername(), notNullValue());
                }
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace(LOG_NO_DUPLICATES);
            questionDaoTestUtil.createQuestionWithAnswersWithComments(
                    (int) (QuestionDaoTestUtil.RESULT_SIZE * 1.5),
                    QuestionDaoTestUtil.COMMENT_RESULT_SIZE);

            final long questionId = 1L;
            final long userId = -1L;
            int page = 0;

            final List<Answer> answers_1 = answerDao.getAnswers(questionId, userId, page);
            final List<Answer> answers_2 = answerDao.getAnswers(questionId, userId, ++page);

            assertThat(answers_1, notNullValue());
            assertThat(answers_2, notNullValue());

            assertThat(answers_1.isEmpty(), equalTo(false));
            assertThat(answers_2.isEmpty(), equalTo(false));

            final int size1 = answers_1.size();
            final int size2 = answers_2.size();

            final long[] ids1 = new long[size1];
            final long[] ids2 = new long[size2];

            for (int i = 0; i < size1; i++) {
                final Answer a = answers_1.get(i);
                ids1[i] = a.getId();
            }
            for (int i = 0; i < size2; i++) {
                final Answer a = answers_2.get(i);
                ids2[i] = a.getId();
            }

            assertThat(ids1, equalTo(Arrays.stream(ids1).distinct().toArray()));
            assertThat(ids2, equalTo(Arrays.stream(ids2).distinct().toArray()));
        }

        @Nested
        class no_result {

            @Test
            void assert_result_equals_null_question_not_exist() {
                logger.trace(LOG_RESULT_NULL);

                final long questionId = 1L;
                final long userId = -1L;
                final int page = 1;

                final List<Answer> answer = answerDao.getAnswers(questionId, userId, page);
                assertThat(answer, equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list_question_exist() {
                logger.trace(LOG_RESULT_EMPTY_LIST);
                questionDaoTestUtil.createQuestion();

                final long questionId = 1L;
                final long userId = -1L;
                final int page = 1;

                final List<Answer> answer = answerDao.getAnswers(questionId, userId, page);
                assertThat(answer, equalTo(Collections.emptyList()));
            }
        }
    }

    @Nested
    class like {
        @Test
        void assert_correct_result() {
            logger.trace(LOG_CORRECT_RESULT);
            answerDaoTestUtil.createAnswer();

            final long questionId = 1L;
            final long userId = 1L;
            final int page = 0;
            final int likes = 15;

            final List<Answer> result = answerDao.getAnswers(questionId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            for (Answer a : result) {
                assertThat(a, notNullValue());
                assertThat(a.getLikes(), equalTo(0));
                answerDaoTestUtil.like(a.getId(), likes);
            }

            final List<Answer> resultLiked = answerDao.getAnswers(questionId, userId, page);
            assertThat(resultLiked, notNullValue());
            assertThat(resultLiked.isEmpty(), equalTo(false));

            for (Answer a : resultLiked) {
                assertThat(a, notNullValue());
                assertThat(a.getLikes(), equalTo(likes));
            }
        }

        @Test
        void assert_correct_keys() {
            logger.trace(LOG_CORRECT_KEYS);

            final long questionId = 1L;
            final long userId = -1L;
            final long answerId = 0L;
            final int answers = 2;
            final int page = 0;
            final int likes = 15;

            answerDaoTestUtil.createManyAnswers(answers);
            answerDaoTestUtil.like(answerId, likes);

            final List<Answer> result = answerDao.getAnswers(questionId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            final Answer answer = result.get(0);
            assertThat(answer.getLikes(), equalTo(likes));
        }

        @Test
        void assert_no_more_than_one() {
            logger.trace(LOG_LIKE_ONE_TIME);
            answerDaoTestUtil.createAnswer();

            final long questionId = 1L;
            final long answerId = 1L;
            final long userId = 1L;
            final int page = 0;

            answerDao.like(userId, answerId);       // 1 - success
            answerDao.like(userId, answerId);       // 2 - ignore

            final List<Answer> result = answerDao.getAnswers(questionId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            final Answer answer = result.get(0);

            assertThat(result, notNullValue());
            assertThat(answer.getLikes(), equalTo(1));
        }

        @Test
        void assert_liked_by_user_caller() {
            logger.trace(LOG_LIKED_STATUS_TRUE);
            answerDaoTestUtil.createAnswer();

            final long questionId = 1L;
            final long answerId = 1L;
            final long userId = 1L;
            final int page = 0;

            answerDao.like(userId, answerId);

            final List<Answer> result = answerDao.getAnswers(questionId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            final Answer answer = result.get(0);

            assertThat(result, notNullValue());
            assertThat(answer.isLiked(), equalTo(true));
        }

        @Test
        void assert_not_liked_by_user_caller() {
            logger.trace(LOG_LIKED_STATUS_FALSE);
            answerDaoTestUtil.createAnswer();

            final long questionId = 1L;
            final long answerId = 1L;
            final long anotherUserId = -1;
            final long userId = 1L;
            final int page = 0;

            answerDao.like(anotherUserId, answerId);

            final List<Answer> result = answerDao.getAnswers(questionId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            final Answer answer = result.get(0);

            assertThat(result, notNullValue());
            assertThat(answer.isLiked(), equalTo(false));
        }
    }

    @Nested
    class delete {
        @Test
        void assert_removes_all_linked_keys() {
            logger.trace(LOG_REMOVES_LINKED_KEYS);
            answerDaoTestUtil.createAnswer();

            final long answerId = 1L;
            final long userId = 1L;

            answerDao.like(userId, answerId);
            answerDao.delete(answerId);

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
        void assert_removes_nested_cache() {
            logger.trace(LOG_REMOVES_NESTED_KEYS);

            /* created many nested keys, which will be removed by links between each other */
            answerDaoTestUtil.createAnswerWithManyComments(3);
            answerDao.delete(1L);

            final Set<String> keys = redisTestUtil.getAllKeys();
            assertThat(keys.size(), equalTo(0));
        }

        @Test
        void no_keys() {
            logger.trace(LOG_NO_EXCEPTIONS);

            final long answerId = 1L;
            assertDoesNotThrow(() -> answerDao.delete(answerId));

            final Set<String> keys = redisTestUtil.getAllKeys();
            assertThat(keys.size(), equalTo(0));
        }
    }
}
