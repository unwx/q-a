package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import qa.cache.CacheRemover;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.cache.entity.like.provider.like.CommentAnswerLikeProvider;
import qa.domain.CommentAnswer;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.MockitoTest;
import qa.util.dao.AnswerDaoTestUtil;
import qa.util.dao.CommentDaoTestUtil;
import qa.util.dao.RedisTestUtil;
import qa.util.hibernate.HibernateSessionFactoryConfigurer;
import qa.util.mock.MockUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@MockitoTest
public class CommentAnswerDaoTest {

    private SessionFactory sessionFactory;
    private CommentAnswerDao commentAnswerDao;
    private AnswerDaoTestUtil answerDaoTestUtil;
    private CommentDaoTestUtil commentDaoTestUtil;
    private RedisTestUtil redisTestUtil;

    private final TestLogger logger = new TestLogger(CommentAnswerDaoTest.class);

    private JedisResourceCenter jedisResourceCenter;

    @BeforeAll
    void init() {
        sessionFactory = HibernateSessionFactoryConfigurer.getSessionFactory();
        jedisResourceCenter = MockUtil.mockJedisCenter();
        PropertySetterFactory propertySetterFactory = Mockito.mock(PropertySetterFactory.class);
        CacheRemover cacheRemover = MockUtil.mockCacheRemover();
        CommentAnswerLikeProvider likesProvider = MockUtil.mockCommentAnswerLikeProvider();

        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
        commentDaoTestUtil = new CommentDaoTestUtil(sessionFactory, jedisResourceCenter);
        commentAnswerDao = new CommentAnswerDao(propertySetterFactory, sessionFactory, jedisResourceCenter, cacheRemover, likesProvider);
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
    class get_comments {

        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            answerDaoTestUtil.createAnswerWithManyComments(AnswerDaoTestUtil.COMMENT_RESULT_SIZE);
            List<CommentAnswer> comments = commentAnswerDao.getComments(1L,  -1L,0);

            assertThat(comments, notNullValue());
            assertThat(comments.size(), greaterThan(0));

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
            logger.trace("assert no duplicates");
            answerDaoTestUtil.createAnswerWithManyComments((int) (AnswerDaoTestUtil.COMMENT_RESULT_SIZE * 1.5));
            List<CommentAnswer> comments1 = commentAnswerDao.getComments(1L,  -1L,0);
            List<CommentAnswer> comments2 = commentAnswerDao.getComments(1L,  -1L,0);

            assertThat(comments1, notNullValue());
            assertThat(comments2, notNullValue());

            assertThat(comments1.size(), greaterThan(0));
            assertThat(comments2.size(), greaterThan(0));

            int size1 = comments1.size();
            int size2 = comments2.size();

            long[] ids1 = new long[size1];
            long[] ids2 = new long[size2];

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
            void assert_result_equals_null_answer_not_exist() {
                logger.trace("assert result equals null when answer not exist");
                assertThat(commentAnswerDao.getComments(1L,  -1L,1), equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list_answer_exist() {
                logger.trace("assert result equals empty list when answer exist");
                answerDaoTestUtil.createAnswer();
                assertThat(commentAnswerDao.getComments(1L, -1L,1), equalTo(Collections.emptyList()));
            }
        }
    }

    @Nested
    class like {
        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            commentDaoTestUtil.createCommentAnswer();
            List<CommentAnswer> result = commentAnswerDao.getComments(1L, -1L, 0);
            assertThat(result, notNullValue());

            for (CommentAnswer c : result) {
                assertThat(c, notNullValue());
                assertThat(c.getLikes(), equalTo(0));
                commentDaoTestUtil.likeCommentAnswer(c.getId(), 15);
            }

            List<CommentAnswer> resultLiked = commentAnswerDao.getComments(1L, -1L, 0);
            assertThat(resultLiked, notNullValue());

            for (CommentAnswer c : resultLiked) {
                assertThat(c, notNullValue());
                assertThat(c.getLikes(), equalTo(15));
            }
        }

        @Test
        void assert_correct_keys() {
            logger.trace("assert correct keys");
            commentDaoTestUtil.createManyCommentAnswers(2);
            commentDaoTestUtil.likeCommentAnswer(0L, 15);

            List<CommentAnswer> result = commentAnswerDao.getComments(1L, -1L, 0);
            assertThat(result, notNullValue());
            CommentAnswer comment = result.get(0);

            assertThat(comment.getLikes(), equalTo(15));
        }

        @Test
        void assert_success() {
            logger.trace("assert success");
            commentDaoTestUtil.createCommentAnswer();
            commentDaoTestUtil.likeCommentAnswer(1L, 5);

            List<CommentAnswer> result = commentAnswerDao.getComments(1L, -1L, 0);
            assertThat(result, notNullValue());
            CommentAnswer comment = result.get(0);

            assertThat(result, notNullValue());
            assertThat(comment.getLikes(), equalTo(5));
        }

        @Test
        void assert_no_more_than_one() {
            logger.trace("assert can't like more than one times");
            commentDaoTestUtil.createCommentAnswer();

            commentAnswerDao.like(1L, 1L);
            commentAnswerDao.like(1L, 1L);

            List<CommentAnswer> result = commentAnswerDao.getComments(1L, 1L, 0);
            assertThat(result, notNullValue());
            CommentAnswer comment = result.get(0);

            assertThat(result, notNullValue());
            assertThat(comment.getLikes(), equalTo(1));
        }

        @Test
        void assert_liked_by_user_caller() {
            logger.trace("assert get user liked status equals true");
            commentDaoTestUtil.createCommentAnswer();
            commentAnswerDao.like(1L, 1L);

            List<CommentAnswer> result = commentAnswerDao.getComments(1L, 1L, 0);
            assertThat(result, notNullValue());
            CommentAnswer comment = result.get(0);

            assertThat(result, notNullValue());
            assertThat(comment.isLiked(), equalTo(true));
        }

        @Test
        void assert_not_liked_by_user_caller() {
            logger.trace("assert get user liked status equals false");
            commentDaoTestUtil.createCommentAnswer();
            commentAnswerDao.like(-1L, 1L);

            List<CommentAnswer> result = commentAnswerDao.getComments(1L, 1L, 0);
            assertThat(result, notNullValue());
            CommentAnswer comment = result.get(0);

            assertThat(result, notNullValue());
            assertThat(comment.isLiked(), equalTo(false));
        }
    }

    @Nested
    class delete {
        @Test
        void assert_success() {
            logger.trace("assert success simple situation");
            commentDaoTestUtil.createCommentAnswer();
            commentAnswerDao.like(1L, 1L);

            commentAnswerDao.delete(1L);

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
            logger.trace("assert success no keys situation");

            assertDoesNotThrow(() -> commentAnswerDao.delete(1L));
            final Set<String> keys = redisTestUtil.getAllKeys();
            assertThat(keys.size(), equalTo(0));
        }
    }
}
