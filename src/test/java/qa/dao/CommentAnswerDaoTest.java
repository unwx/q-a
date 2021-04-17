package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import qa.cache.JedisResourceCenter;
import qa.domain.CommentAnswer;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.MockitoTest;
import qa.util.dao.AnswerDaoTestUtil;
import qa.util.hibernate.HibernateSessionFactoryUtil;
import qa.util.mock.JedisMockTestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@MockitoTest
public class CommentAnswerDaoTest {

    private SessionFactory sessionFactory;
    private CommentAnswerDao commentAnswerDao;
    private AnswerDaoTestUtil answerDaoTestUtil;
    private final TestLogger logger = new TestLogger(CommentAnswerDaoTest.class);

    @BeforeAll
    void init() {
        JedisResourceCenter jedisResourceCenter = JedisMockTestUtil.mockJedisFactory();
        PropertySetterFactory propertySetterFactory = Mockito.mock(PropertySetterFactory.class);

        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
        commentAnswerDao = new CommentAnswerDao(propertySetterFactory);
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
    }

    @Nested
    class get_comments {

        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            answerDaoTestUtil.createAnswerWithManyComments(AnswerDaoTestUtil.COMMENT_RESULT_SIZE);
            List<CommentAnswer> comments = commentAnswerDao.getComments(1L, 0);

            assertThat(comments, notNullValue());
            assertThat(comments.size(), greaterThan(0));

            for (CommentAnswer c : comments) {
                assertThat(c, notNullValue());
                assertThat(c.getId(), notNullValue());
                assertThat(c.getText(), notNullValue());
                assertThat(c.getCreationDate(), notNullValue());
                assertThat(c.getAuthor().getUsername(), notNullValue());
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace("assert no duplicates");
            answerDaoTestUtil.createAnswerWithManyComments((int) (AnswerDaoTestUtil.COMMENT_RESULT_SIZE * 1.5));
            List<CommentAnswer> comments1 = commentAnswerDao.getComments(1L, 0);
            List<CommentAnswer> comments2 = commentAnswerDao.getComments(1L, 0);

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
                assertThat(commentAnswerDao.getComments(1L, 1), equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list_answer_exist() {
                logger.trace("assert result equals empty list when answer exist");
                answerDaoTestUtil.createAnswer();
                assertThat(commentAnswerDao.getComments(1L, 1), equalTo(Collections.emptyList()));
            }
        }
    }
}
