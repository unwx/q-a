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
import qa.cache.like.CommentAnswerLikeProvider;
import qa.dao.util.HibernateSessionFactoryConfigurer;
import qa.domain.CommentAnswer;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.MockitoTest;
import redis.clients.jedis.Jedis;
import util.dao.AnswerDaoTestUtil;
import util.dao.CommentDaoTestUtil;
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
public class CommentAnswerDaoTest {


    private CommentAnswerDao commentAnswerDao;
    private AnswerDaoTestUtil answerDaoTestUtil;
    private CommentDaoTestUtil commentDaoTestUtil;

    private SessionFactory sessionFactory;
    private JedisResourceCenter jedisResourceCenter;

    private RedisTestUtil redisTestUtil;

    private static final String LOG_CORRECT_RESULT          = "assert correct result";
    private static final String LOG_NO_DUPLICATES           = "assert no duplicates";
    private static final String LOG_RESULT_NULL             = "assert result equals null, when answer not exist";
    private static final String LOG_RESULT_EMPTY_LIST       = "assert result equals empty list, when answer exist";
    private static final String LOG_CORRECT_KEYS            = "assert result equals empty list, when answer exist";
    private static final String LOG_LIKE_ONE_TIME           = "assert can't like more than one times";
    private static final String LOG_LIKED_STATUS_TRUE       = "assert get user liked status equals true";
    private static final String LOG_LIKED_STATUS_FALSE      = "assert get user liked status equals false";
    private static final String LOG_REMOVES_LINKED_KEYS     = "assert removes all linked keys";
    private static final String LOG_NO_EXCEPTIONS           = "assert no exceptions";

    private final TestLogger logger = new TestLogger(CommentAnswerDaoTest.class);

    @BeforeAll
    void init() {
        final PropertySetterFactory propertySetterFactory = Mockito.spy(PropertySetterFactory.class);
        final CommentAnswerLikeProvider likesProvider = MockUtil.mockCommentAnswerLikeProvider();

        sessionFactory = HibernateSessionFactoryConfigurer.getSessionFactory();
        jedisResourceCenter = MockUtil.mockJedisCenter();

        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
        commentDaoTestUtil = new CommentDaoTestUtil(sessionFactory, jedisResourceCenter);
        commentAnswerDao = new CommentAnswerDao(propertySetterFactory, sessionFactory, jedisResourceCenter, likesProvider);
        redisTestUtil = new RedisTestUtil(jedisResourceCenter);
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
    class get_comments {

        @Test
        void assert_correct_result() {
            logger.trace(LOG_CORRECT_RESULT);
            answerDaoTestUtil.createAnswerWithManyComments(AnswerDaoTestUtil.COMMENT_RESULT_SIZE);

            final long answerId = 1L;
            final long userId = -1L;
            final int page = 0;

            final List<CommentAnswer> comments = commentAnswerDao.getComments(answerId,  userId, page);
            assertThat(comments, notNullValue());
            assertThat(comments.isEmpty(), equalTo(false));

            for (CommentAnswer c : comments) {
                assertThat(c, notNullValue());
                assertThat(c.getId(), notNullValue());
                assertThat(c.getText(), notNullValue());
                assertThat(c.getCreationDate(), notNullValue());
                assertThat(c.getAuthor().getUsername(), notNullValue());

                assertThat(c.getLikes(), equalTo(0));
                assertThat(c.isLiked(), equalTo(false));
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace(LOG_NO_DUPLICATES);
            answerDaoTestUtil.createAnswerWithManyComments((int) (AnswerDaoTestUtil.COMMENT_RESULT_SIZE * 1.5));

            final long answerId = 1L;
            final long userId = -1L;
            int page = 0;

            final List<CommentAnswer> comments_1 = commentAnswerDao.getComments(answerId,  userId, page);
            final List<CommentAnswer> comments_2 = commentAnswerDao.getComments(answerId,  userId, ++page);

            assertThat(comments_1, notNullValue());
            assertThat(comments_2, notNullValue());

            assertThat(comments_1.isEmpty(), equalTo(false));
            assertThat(comments_2.isEmpty(), equalTo(false));

            final int size1 = comments_1.size();
            final int size2 = comments_2.size();

            final long[] ids1 = new long[size1];
            final long[] ids2 = new long[size2];

            for (int i = 0; i < size1; i++) {
                final CommentAnswer comment = comments_1.get(i);
                ids1[i] = comment.getId();
            }
            for (int i = 0; i < size2; i++) {
                final CommentAnswer comment = comments_2.get(i);
                ids2[i] = comment.getId();
            }

            assertThat(ids1, equalTo(Arrays.stream(ids1).distinct().toArray()));
            assertThat(ids2, equalTo(Arrays.stream(ids2).distinct().toArray()));
        }

        @Nested
        class no_result {

            @Test
            void assert_result_equals_null_answer_not_exist() {
                logger.trace(LOG_RESULT_NULL);

                final long answerId = 1L;
                final long userId = -1L;
                final int page = 1;

                final List<CommentAnswer> comments = commentAnswerDao.getComments(answerId,  userId, page);
                assertThat(comments, equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list_answer_exist() {
                logger.trace(LOG_RESULT_EMPTY_LIST);
                answerDaoTestUtil.createAnswer();

                final long answerId = 1L;
                final long userId = -1L;
                final int page = 1;

                final List<CommentAnswer> comments = commentAnswerDao.getComments(answerId,  userId, page);
                assertThat(comments, equalTo(Collections.emptyList()));
            }
        }
    }

    @Nested
    class like {
        @Test
        void assert_correct_result() {
            logger.trace(LOG_CORRECT_RESULT);
            commentDaoTestUtil.createCommentAnswer();

            final long answerId = 1L;
            final long userId = 1L;
            final int page = 0;
            final int likes = 15;

            final List<CommentAnswer> result = commentAnswerDao.getComments(answerId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            for (CommentAnswer c : result) {
                assertThat(c, notNullValue());
                assertThat(c.getLikes(), equalTo(0));
                commentDaoTestUtil.likeCommentAnswer(c.getId(), likes);
            }

            final List<CommentAnswer> resultLiked = commentAnswerDao.getComments(answerId, userId, page);
            assertThat(resultLiked, notNullValue());
            assertThat(resultLiked.isEmpty(), equalTo(false));

            for (CommentAnswer c : resultLiked) {
                assertThat(c, notNullValue());
                assertThat(c.getLikes(), equalTo(likes));
            }
        }

        @Test
        void assert_correct_keys() {
            logger.trace(LOG_CORRECT_KEYS);

            final long answerId = 1L;
            final long commentId = 0L;
            final long userId = -1L;
            final int comments = 2;
            final int page = 0;
            final int likes = 15;

            commentDaoTestUtil.createManyCommentAnswers(comments);
            commentDaoTestUtil.likeCommentAnswer(commentId, likes);

            final List<CommentAnswer> result = commentAnswerDao.getComments(answerId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            final CommentAnswer comment = result.get(0);
            assertThat(result, notNullValue());
            assertThat(comment.getLikes(), equalTo(likes));
        }

        @Test
        void assert_no_more_than_one() {
            logger.trace(LOG_LIKE_ONE_TIME);
            commentDaoTestUtil.createCommentAnswer();

            final long answerId = 1L;
            final long userId = 1L;
            final long commentId = 1L;
            final int page = 0;

            commentAnswerDao.like(userId, commentId);      // 1 - success
            commentAnswerDao.like(userId, commentId);      // 2 - ignore

            final List<CommentAnswer> result = commentAnswerDao.getComments(answerId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            final CommentAnswer comment = result.get(0);
            assertThat(result, notNullValue());
            assertThat(comment.getLikes(), equalTo(1));
        }

        @Test
        void assert_liked_by_user_caller() {
            logger.trace(LOG_LIKED_STATUS_TRUE);
            commentDaoTestUtil.createCommentAnswer();

            final long answerId = 1L;
            final long userId = 1L;
            final long commentId = 1L;
            final int page = 0;

            commentAnswerDao.like(userId, commentId);

            final List<CommentAnswer> result = commentAnswerDao.getComments(answerId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            final CommentAnswer comment = result.get(0);
            assertThat(result, notNullValue());
            assertThat(comment.isLiked(), equalTo(true));
        }

        @Test
        void assert_not_liked_by_user_caller() {
            logger.trace(LOG_LIKED_STATUS_FALSE);
            commentDaoTestUtil.createCommentAnswer();

            final long answerId = 1L;
            final long anotherUserId = -1L;
            final long userId = 1L;
            final long commentId = 1L;
            final int page = 0;

            commentAnswerDao.like(anotherUserId, commentId);

            final List<CommentAnswer> result = commentAnswerDao.getComments(answerId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            final CommentAnswer comment = result.get(0);
            assertThat(result, notNullValue());
            assertThat(comment.isLiked(), equalTo(false));
        }
    }

    @Nested
    class delete {
        @Test
        void assert_success() {
            logger.trace(LOG_REMOVES_LINKED_KEYS);
            commentDaoTestUtil.createCommentAnswer();

            final long userId = 1L;
            final long commentId = 1L;

            commentAnswerDao.like(userId, commentId);
            commentAnswerDao.delete(commentId);

            final Set<String> keys = redisTestUtil.getAllKeys();
            /*
             * 3 keys created: {comment-answer-like (id:counter), user-comment-answer (id:id), comment-answer-user (id:id)}
             * delete comment-answer -> delete `comment-answer-like` key,
             *  delete all associations with user-comment-answer,
             *  delete comment-answer-user key;
             * 1 key remaining
             * if user-comment-answer is empty -> no keys remaining
             */
            assertThat(keys.size(), equalTo(0));
        }

        @Test
        void no_keys() {
            logger.trace(LOG_NO_EXCEPTIONS);

            final long commentId = 1L;
            assertDoesNotThrow(() -> commentAnswerDao.delete(commentId));

            final Set<String> keys = redisTestUtil.getAllKeys();
            assertThat(keys.size(), equalTo(0));
        }
    }
}
