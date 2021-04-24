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
import qa.domain.CommentQuestion;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.MockitoTest;
import qa.util.dao.CommentDaoTestUtil;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.hibernate.HibernateSessionFactoryConfigurer;
import qa.util.mock.JedisMockTestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@MockitoTest
public class CommentQuestionDaoTest {

    private SessionFactory sessionFactory;
    private CommentQuestionDao commentQuestionDao;
    private CommentDaoTestUtil commentDaoTestUtil;
    private QuestionDaoTestUtil questionDaoTestUtil;

    private final TestLogger logger = new TestLogger(CommentQuestionDaoTest.class);

    private JedisResourceCenter jedisResourceCenter;

    @BeforeAll
    void init() {
        sessionFactory = HibernateSessionFactoryConfigurer.getSessionFactory();
        jedisResourceCenter = JedisMockTestUtil.mockJedisFactory();
        PropertySetterFactory propertySetterFactory = Mockito.mock(PropertySetterFactory.class);

        commentQuestionDao = new CommentQuestionDao(propertySetterFactory, sessionFactory, jedisResourceCenter);
        commentDaoTestUtil = new CommentDaoTestUtil(sessionFactory, jedisResourceCenter);
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);
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
            questionDaoTestUtil.createQuestionWithComments((int) (QuestionDaoTestUtil.COMMENT_RESULT_SIZE * 1.5));
            for (int i = 0; i < 2; i++) {
                List<CommentQuestion> comments = commentQuestionDao.getComments(1L,-1L,  i);
                assertThat(comments, notNullValue());
                assertThat(comments.size(), greaterThan(0));
                for (int y = 0; y < comments.size(); y++) {
                    assertThat(comments, notNullValue());
                    assertThat(comments.get(y), notNullValue());
                    assertThat(comments.get(y).getId(), notNullValue());
                    assertThat(comments.get(y).getText(), notNullValue());
                    assertThat(comments.get(y).getCreationDate(), notNullValue());
                    assertThat(comments.get(y).getAuthor(), notNullValue());
                    assertThat(comments.get(y).getLikes(), equalTo(0));
                    assertThat(comments.get(y).isLiked(), equalTo(false));
                }
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace("assert no duplicates");
            questionDaoTestUtil.createQuestionWithComments((int) (QuestionDaoTestUtil.COMMENT_RESULT_SIZE * 1.5));

            List<CommentQuestion> comments1 = commentQuestionDao.getComments(1L, -1L, 0);
            List<CommentQuestion> comments2 = commentQuestionDao.getComments(1L, -1L, 1);

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
            void assert_result_equals_null_question_not_exist() {
                logger.trace("assert result equals null when question not exist");
                assertThat(commentQuestionDao.getComments(1L, -1L, 1), equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list_question_exist() {
                logger.trace("assert result equals empty list when question exist");
                questionDaoTestUtil.createQuestion();
                assertThat(commentQuestionDao.getComments(1L, -1L, 1), equalTo(Collections.emptyList()));
            }
        }
    }

    @Nested
    class like {
        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            commentDaoTestUtil.createCommentQuestion();
            List<CommentQuestion> result = commentQuestionDao.getComments(1L, -1L, 0);
            assertThat(result, notNullValue());

            for (CommentQuestion c : result) {
                assertThat(c, notNullValue());
                assertThat(c.getLikes(), equalTo(0));
                commentDaoTestUtil.like(c.getId(), 15);
            }

            List<CommentQuestion> resultLiked = commentQuestionDao.getComments(1L, -1L, 0);
            assertThat(resultLiked, notNullValue());

            for (CommentQuestion c : resultLiked) {
                assertThat(c, notNullValue());
                assertThat(c.getLikes(), equalTo(15));
            }
        }

        @Test
        void assert_correct_keys() {
            logger.trace("assert correct keys");
            commentDaoTestUtil.createManyCommentQuestions(2);
            commentDaoTestUtil.like(0L, 15);

            List<CommentQuestion> result = commentQuestionDao.getComments(1L, -1L, 0);
            assertThat(result, notNullValue());
            CommentQuestion comment = result.get(0);

            assertThat(comment.getLikes(), equalTo(15));
        }

        @Test
        void assert_success() {
            logger.trace("assert success");
            commentDaoTestUtil.createCommentQuestion();
            commentDaoTestUtil.like(1L, 5);

            List<CommentQuestion> result = commentQuestionDao.getComments(1L, -1L, 0);
            assertThat(result, notNullValue());
            CommentQuestion comment = result.get(0);

            assertThat(result, notNullValue());
            assertThat(comment.getLikes(), equalTo(5));
        }

        @Test
        void assert_no_more_than_one() {
            logger.trace("assert can't like more than one times");
            commentDaoTestUtil.createCommentQuestion();

            commentQuestionDao.like(1L, 1L);
            commentQuestionDao.like(1L, 1L);

            List<CommentQuestion> result = commentQuestionDao.getComments(1L, 1L, 0);
            assertThat(result, notNullValue());
            CommentQuestion comment = result.get(0);

            assertThat(result, notNullValue());
            assertThat(comment.getLikes(), equalTo(1));
        }

        @Test
        void assert_liked_by_user_caller() {
            logger.trace("assert get user liked status equals true");
            commentDaoTestUtil.createCommentQuestion();
            commentQuestionDao.like(1L, 1L);

            List<CommentQuestion> result = commentQuestionDao.getComments(1L, 1L, 0);
            assertThat(result, notNullValue());
            CommentQuestion comment = result.get(0);

            assertThat(result, notNullValue());
            assertThat(comment.isLiked(), equalTo(true));
        }

        @Test
        void assert_not_liked_by_user_caller() {
            logger.trace("assert get user liked status equals false");
            commentDaoTestUtil.createCommentQuestion();
            commentQuestionDao.like(-1L, 1L);

            List<CommentQuestion> result = commentQuestionDao.getComments(1L, 1L, 0);
            assertThat(result, notNullValue());
            CommentQuestion comment = result.get(0);

            assertThat(result, notNullValue());
            assertThat(comment.isLiked(), equalTo(false));
        }
    }
}