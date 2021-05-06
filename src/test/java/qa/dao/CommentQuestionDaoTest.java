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
import qa.cache.like.CommentQuestionLikeProvider;
import qa.dao.util.HibernateSessionFactoryConfigurer;
import qa.domain.CommentQuestion;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.MockitoTest;
import redis.clients.jedis.Jedis;
import util.dao.CommentDaoTestUtil;
import util.dao.QuestionDaoTestUtil;
import util.dao.RedisTestUtil;
import util.dao.TruncateUtil;
import util.mock.MockUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@MockitoTest
public class CommentQuestionDaoTest {


    private CommentQuestionDao commentQuestionDao;
    private CommentDaoTestUtil commentDaoTestUtil;
    private QuestionDaoTestUtil questionDaoTestUtil;

    private RedisTestUtil redisTestUtil;

    private SessionFactory sessionFactory;
    private JedisResourceCenter jedisResourceCenter;

    private static final String LOG_CORRECT_RESULT          = "assert correct result";
    private static final String LOG_NO_DUPLICATES           = "assert no duplicates";
    private static final String LOG_RESULT_NULL             = "assert result equals null, when answer not exist";
    private static final String LOG_RESULT_EMPTY_LIST       = "assert result equals empty list, when question exist";
    private static final String LOG_CORRECT_KEYS            = "assert result equals empty list, when question exist";
    private static final String LOG_LIKE_ONE_TIME           = "assert can't like more than one times";
    private static final String LOG_LIKED_STATUS_TRUE       = "assert get user liked status equals true";
    private static final String LOG_LIKED_STATUS_FALSE      = "assert get user liked status equals false";
    private static final String LOG_REMOVES_LINKED_KEYS     = "assert removes all linked keys";
    private static final String LOG_NO_EXCEPTIONS           = "assert no exceptions";

    private final TestLogger logger = new TestLogger(CommentQuestionDaoTest.class);

    @BeforeAll
    void init() {
        final PropertySetterFactory propertySetterFactory = Mockito.spy(PropertySetterFactory.class);
        final CommentQuestionLikeProvider likesProvider = MockUtil.mockCommentQuestionLikeProvider();

        sessionFactory = HibernateSessionFactoryConfigurer.getSessionFactory();
        jedisResourceCenter = MockUtil.mockJedisCenter();

        commentQuestionDao = new CommentQuestionDao(propertySetterFactory, sessionFactory, jedisResourceCenter, likesProvider);
        commentDaoTestUtil = new CommentDaoTestUtil(sessionFactory, jedisResourceCenter);
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);
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
            questionDaoTestUtil.createQuestionWithComments((int) (QuestionDaoTestUtil.COMMENT_RESULT_SIZE * 1.5));

            final long questionId = 1L;
            final long userId = -1L;
            final int page = 0;

            final List<CommentQuestion> comments = commentQuestionDao.getComments(questionId, userId, page);
            assertThat(comments, notNullValue());
            assertThat(comments.isEmpty(), equalTo(false));

            for (CommentQuestion cq : comments) {
                assertThat(cq, notNullValue());
                assertThat(cq.getId(), notNullValue());
                assertThat(cq.getText(), notNullValue());
                assertThat(cq.getCreationDate(), notNullValue());
                assertThat(cq.getAuthor(), notNullValue());

                assertThat(cq.getLikes(), equalTo(0));
                assertThat(cq.isLiked(), equalTo(false));
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace(LOG_NO_DUPLICATES);
            questionDaoTestUtil.createQuestionWithComments((int) (QuestionDaoTestUtil.COMMENT_RESULT_SIZE * 1.5));

            final long questionId = 1L;
            final long userId = -1L;
            int page = 0;

            final List<CommentQuestion> comments1 = commentQuestionDao.getComments(questionId, userId, page);
            final List<CommentQuestion> comments2 = commentQuestionDao.getComments(questionId, userId, ++page);

            assertThat(comments1, notNullValue());
            assertThat(comments2, notNullValue());

            assertThat(comments1.size(), greaterThan(0));
            assertThat(comments2.size(), greaterThan(0));

            final int size1 = comments1.size();
            final int size2 = comments2.size();

            final long[] ids1 = new long[size1];
            final long[] ids2 = new long[size2];

            for (int i = 0; i < size1; i++) {
                ids1[i] = comments1.get(i).getId();
            }
            for (int i = 0; i < size2; i++) {
                ids2[i] = comments2.get(i).getId();
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

                final List<CommentQuestion> comment = commentQuestionDao.getComments(questionId, userId, page);
                assertThat(comment, equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list_question_exist() {
                logger.trace(LOG_RESULT_EMPTY_LIST);
                questionDaoTestUtil.createQuestion();

                final long questionId = 1L;
                final long userId = -1L;
                final int page = 1;

                final List<CommentQuestion> comment = commentQuestionDao.getComments(questionId, userId, page);
                assertThat(comment, equalTo(Collections.emptyList()));
            }
        }
    }

    @Nested
    class like {
        @Test
        void assert_correct_result() {
            logger.trace(LOG_CORRECT_RESULT);
            commentDaoTestUtil.createCommentQuestion();

            final long questionId = 1L;
            final long userId = 1L;
            final int page = 0;
            final int likes = 15;

            final List<CommentQuestion> result = commentQuestionDao.getComments(questionId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            for (CommentQuestion c : result) {
                assertThat(c, notNullValue());
                assertThat(c.getLikes(), equalTo(0));
                commentDaoTestUtil.likeCommentQuestion(c.getId(), likes);
            }

            final List<CommentQuestion> resultLiked = commentQuestionDao.getComments(questionId, userId, page);
            assertThat(resultLiked, notNullValue());
            assertThat(resultLiked.isEmpty(), equalTo(false));

            for (CommentQuestion c : resultLiked) {
                assertThat(c, notNullValue());
                assertThat(c.getLikes(), equalTo(15));
            }
        }

        @Test
        void assert_correct_keys() {
            logger.trace(LOG_CORRECT_KEYS);

            final long questionId = 1L;
            final long userId = 1L;
            final long commentId = 0L;
            final int page = 0;
            final int comments = 2;
            final int likes = 15;

            commentDaoTestUtil.createManyCommentQuestions(comments);
            commentDaoTestUtil.likeCommentQuestion(commentId, likes);

            final List<CommentQuestion> result = commentQuestionDao.getComments(questionId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            final CommentQuestion comment = result.get(0);
            assertThat(comment.getLikes(), equalTo(likes));
        }

        @Test
        void assert_no_more_than_one() {
            logger.trace(LOG_LIKE_ONE_TIME);
            commentDaoTestUtil.createCommentQuestion();

            final long questionId = 1L;
            final long userId = 1L;
            final long commentId = 1L;
            final int page = 0;

            commentQuestionDao.like(userId, commentId);     // 1 - success
            commentQuestionDao.like(userId, commentId);     // 2 - ignore

            final List<CommentQuestion> result = commentQuestionDao.getComments(questionId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            final CommentQuestion comment = result.get(0);
            assertThat(result, notNullValue());
            assertThat(comment.getLikes(), equalTo(1));
        }

        @Test
        void assert_liked_by_user_caller() {
            logger.trace(LOG_LIKED_STATUS_TRUE);
            commentDaoTestUtil.createCommentQuestion();

            final long questionId = 1L;
            final long userId = 1L;
            final long commentId = 1L;
            final int page = 0;

            commentQuestionDao.like(userId, commentId);

            final List<CommentQuestion> result = commentQuestionDao.getComments(questionId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            final CommentQuestion comment = result.get(0);
            assertThat(result, notNullValue());
            assertThat(comment.isLiked(), equalTo(true));
        }

        @Test
        void assert_not_liked_by_user_caller() {
            logger.trace(LOG_LIKED_STATUS_FALSE);
            commentDaoTestUtil.createCommentQuestion();

            final long questionId = 1L;
            final long anotherUserId = -1L;
            final long userId = 1L;
            final long commentId = 1L;
            final int page = 0;

            commentQuestionDao.like(anotherUserId, commentId);

            final List<CommentQuestion> result = commentQuestionDao.getComments(questionId, userId, page);
            assertThat(result, notNullValue());
            assertThat(result.isEmpty(), equalTo(false));

            final CommentQuestion comment = result.get(0);
            assertThat(result, notNullValue());
            assertThat(comment.isLiked(), equalTo(false));
        }
    }

    @Nested
    class delete {
        @Test
        void assert_success() {
            logger.trace(LOG_REMOVES_LINKED_KEYS);
            commentDaoTestUtil.createCommentQuestion();
            commentQuestionDao.like(1L, 1L);

            final long commentId = 1L;
            commentQuestionDao.delete(commentId);

            final Set<String> keys = redisTestUtil.getAllKeys();
            /*
             * 3 keys created: {comment-question-like (id:counter), user-comment-question (id:id), comment-question-user (id:id)}
             * delete comment-question -> delete `comment-question-like` key,
             *  delete all associations with user-comment-question,
             *  delete comment-question-user key;
             * 1 key remaining
             * if user-comment-question is empty -> no keys remaining
             */
            assertThat(keys.size(), equalTo(0));
        }

        @Test
        void no_keys() {
            logger.trace(LOG_NO_EXCEPTIONS);

            final long commentId = 1L;
            assertDoesNotThrow(() -> commentQuestionDao.delete(commentId));

            final Set<String> keys = redisTestUtil.getAllKeys();
            assertThat(keys.size(), equalTo(0));
        }
    }
}