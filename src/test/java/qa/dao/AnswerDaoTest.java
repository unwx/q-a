package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.dao.query.AnswerQueryFactory;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.setters.PropertySetterFactory;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AnswerDaoTest {

    private AnswerDao answerDao;
    private SessionFactory sessionFactory;
    private QuestionDaoTestUtil questionDaoTestUtil;

    @BeforeAll
    void init() {
        PropertySetterFactory propertySetterFactory = Mockito.mock(PropertySetterFactory.class);
        AnswerQueryFactory answerQueryFactory = Mockito.spy(new AnswerQueryFactory());

        answerDao = new AnswerDao(propertySetterFactory, answerQueryFactory);
        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory);
    }

    @BeforeEach
    void truncate() {
        try(Session session = sessionFactory.openSession()) {
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
            questionDaoTestUtil.createQuestionWithAnswersWithComments(
                    (int) (QuestionDaoTestUtil.RESULT_SIZE * 1.5),
                    QuestionDaoTestUtil.COMMENT_RESULT_SIZE);

            for (int i = 0; i < 2; i++) {
                List<Answer> answers = answerDao.getAnswers(1L, i);
                assertThat(answers, notNullValue());
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
            questionDaoTestUtil.createQuestionWithAnswersWithComments(
                    (int) (QuestionDaoTestUtil.RESULT_SIZE * 1.5),
                    QuestionDaoTestUtil.COMMENT_RESULT_SIZE);

            List<Answer> answers1 = answerDao.getAnswers(1L, 0);
            List<Answer> answers2 = answerDao.getAnswers(1L, 1);
            assertThat(answers1, notNullValue());
            assertThat(answers2, notNullValue());

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
            void assert_result_equal_null_question_not_exist() {
                assertThat(answerDao.getAnswers(1L, 1), equalTo(null));
            }

            @Test
            void assert_result_equal_empty_list_question_exist() {
                questionDaoTestUtil.createQuestion();
                assertThat(answerDao.getAnswers(1L, 1), equalTo(Collections.emptyList()));
            }
        }
    }
}
