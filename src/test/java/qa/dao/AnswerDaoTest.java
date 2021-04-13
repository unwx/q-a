package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import qa.dao.query.AnswerQueryFactory;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.MockitoTest;
import qa.util.dao.AnswerDaoTestUtil;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@MockitoTest
public class AnswerDaoTest {

    private AnswerDao answerDao;
    private SessionFactory sessionFactory;
    private QuestionDaoTestUtil questionDaoTestUtil;
    private AnswerDaoTestUtil answerDaoTestUtil;

    private final TestLogger logger = new TestLogger(AnswerDaoTest.class);

    @BeforeAll
    void init() {
        PropertySetterFactory propertySetterFactory = Mockito.mock(PropertySetterFactory.class);
        AnswerQueryFactory answerQueryFactory = Mockito.spy(new AnswerQueryFactory());

        answerDao = new AnswerDao(propertySetterFactory, answerQueryFactory);
        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory);
        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory);
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
    class get_full_answers {

        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            questionDaoTestUtil.createQuestionWithAnswersWithComments(
                    (int) (QuestionDaoTestUtil.RESULT_SIZE * 1.5),
                    QuestionDaoTestUtil.COMMENT_RESULT_SIZE);

            for (int i = 0; i < 2; i++) {
                List<Answer> answers = answerDao.getAnswers(1L, i);
                assertThat(answers, notNullValue());
                assertThat(answers.size(), greaterThan(0));
                for (Answer a : answers) {
                    assertThat(a, notNullValue());
                    assertThat(a.getId(), notNullValue());
                    assertThat(a.getText(), notNullValue());
                    assertThat(a.getAnswered(), notNullValue());
                    assertThat(a.getCreationDate(), notNullValue());

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

            List<Answer> answers1 = answerDao.getAnswers(1L, 0);
            List<Answer> answers2 = answerDao.getAnswers(1L, 1);

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
                assertThat(answerDao.getAnswers(1L, 1), equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list_question_exist() {
                logger.trace("assert result equals empty list - when question exist");
                questionDaoTestUtil.createQuestion();
                assertThat(answerDao.getAnswers(1L, 1), equalTo(Collections.emptyList()));
            }
        }
    }

    @Nested
    class get_answer_comments {

        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            answerDaoTestUtil.createAnswerWithManyComments(AnswerDaoTestUtil.COMMENT_RESULT_SIZE);
            List<CommentAnswer> comments = answerDao.getAnswerComments(1L, 0);

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
            List<CommentAnswer> comments1 = answerDao.getAnswerComments(1L, 0);
            List<CommentAnswer> comments2 = answerDao.getAnswerComments(1L, 0);

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
                assertThat(answerDao.getAnswerComments(1L, 1), equalTo(null));
            }

            @Test
            void assert_result_equals_empty_list_answer_exist() {
                logger.trace("assert result equals empty list when answer exist");
                answerDaoTestUtil.createAnswer();
                assertThat(answerDao.getAnswerComments(1L, 1), equalTo(Collections.emptyList()));
            }
        }
    }
}
