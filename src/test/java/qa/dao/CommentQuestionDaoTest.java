package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import qa.domain.CommentQuestion;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.MockitoTest;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@MockitoTest
public class CommentQuestionDaoTest {

    private SessionFactory sessionFactory;
    private CommentQuestionDao commentQuestionDao;
    private QuestionDaoTestUtil questionDaoTestUtil;
    private final TestLogger logger = new TestLogger(CommentQuestionDaoTest.class);

    @BeforeAll
    void init() {
        PropertySetterFactory propertySetterFactory = Mockito.mock(PropertySetterFactory.class);

        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
        commentQuestionDao = new CommentQuestionDao(propertySetterFactory);
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory);
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
            questionDaoTestUtil.createQuestionWithComments((int) (QuestionDaoTestUtil.COMMENT_RESULT_SIZE * 1.5));
            for (int i = 0; i < 2; i++) {
                List<CommentQuestion> comments = commentQuestionDao.getComments(1L, i);
                assertThat(comments, notNullValue());
                assertThat(comments.size(), greaterThan(0));
                for (int y = 0; y < comments.size(); y++) {
                    assertThat(comments, notNullValue());
                    assertThat(comments.get(y), notNullValue());
                    assertThat(comments.get(y).getId(), notNullValue());
                    assertThat(comments.get(y).getText(), notNullValue());
                    assertThat(comments.get(y).getCreationDate(), notNullValue());
                    assertThat(comments.get(y).getAuthor(), notNullValue());
                }
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace("assert no duplicates");
            questionDaoTestUtil.createQuestionWithComments((int) (QuestionDaoTestUtil.COMMENT_RESULT_SIZE * 1.5));

            List<CommentQuestion> comments1 = commentQuestionDao.getComments(1L, 0);
            List<CommentQuestion> comments2 = commentQuestionDao.getComments(1L, 1);

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
                assertThat(commentQuestionDao.getComments(1L, 1), equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list_question_exist() {
                logger.trace("assert result equals empty list when question exist");
                questionDaoTestUtil.createQuestion();
                assertThat(commentQuestionDao.getComments(1L, 1), equalTo(Collections.emptyList()));
            }
        }
    }
}
