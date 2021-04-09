package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.dao.query.AnswerQueryFactory;
import qa.dao.query.QuestionQueryFactory;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.CommentQuestion;
import qa.domain.Question;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.question.QuestionViewDto;
import qa.util.dao.AnswerDaoTestUtil;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class QuestionDaoTest {

    private QuestionDao questionDao;
    private SessionFactory sessionFactory;
    private QuestionDaoTestUtil questionDaoTestUtil;
    private AnswerDaoTestUtil answerDaoTestUtil;

    @BeforeAll
    void init() {
        PropertySetterFactory propertySetterFactory = Mockito.mock(PropertySetterFactory.class);
        QuestionQueryFactory questionQueryFactory = Mockito.spy(new QuestionQueryFactory());
        AnswerQueryFactory answerQueryFactory = Mockito.spy(new AnswerQueryFactory());

        questionDao = new QuestionDao(propertySetterFactory, questionQueryFactory, answerQueryFactory);
        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory);
        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory);
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
    class get_full_question {
        @Test
        void assert_correct_result() {
            questionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(6, 3);
            Question q = questionDao.getFullQuestion(1L);

            assertThat(q, notNullValue());
            assertThat(q.getId(), equalTo(1L));
            assertThat(q.getText(), notNullValue());
            assertThat(q.getTitle(), notNullValue());
            assertThat(q.getCreationDate(), notNullValue());
            assertThat(q.getLastActivity(), notNullValue());
            assertThat(q.getTags(), notNullValue());

            assertThat(q.getAuthor(), notNullValue());
            assertThat(q.getAuthor().getUsername(), notNullValue());

            assertThat(q.getAnswers(), notNullValue());
            for (Answer a : q.getAnswers()) {
                assertThat(a.getId(), notNullValue());
                assertThat(a.getText(), notNullValue());
                assertThat(a.getAnswered(), notNullValue());
                assertThat(a.getCreationDate(), notNullValue());
                assertThat(a.getAuthor(), notNullValue());
                assertThat(a.getAuthor().getUsername(), notNullValue());

                assertThat(a.getComments(), notNullValue());
                assertThat(a.getComments().size(), lessThan(QuestionDaoTestUtil.COMMENT_RESULT_SIZE + 1));
                for (CommentAnswer c : a.getComments()) {
                    assertThat(c.getId(), notNullValue());
                    assertThat(c.getText(), notNullValue());
                    assertThat(c.getCreationDate(), notNullValue());
                    assertThat(c.getAuthor(), notNullValue());
                    assertThat(c.getAuthor().getUsername(), notNullValue());
                }
            }

            assertThat(q.getComments(), notNullValue());
            assertThat(q.getComments().size(), lessThan(QuestionDaoTestUtil.COMMENT_RESULT_SIZE + 1));
            for (int i = 0; i < q.getComments().size(); i++) {
                assertThat(q.getComments().get(i).getAuthor(), notNullValue());
                assertThat(q.getComments().get(i).getAuthor().getUsername(), notNullValue());
                assertThat(q.getComments().get(i).getId(), notNullValue());
                assertThat(q.getComments().get(i).getText(), notNullValue());
                assertThat(q.getComments().get(i).getCreationDate(), notNullValue());
            }
        }

        @Test
        void assert_no_duplicates() {
            questionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(6, 3);
            Question q = questionDao.getFullQuestion(1L);

            assertThat(q, notNullValue());
            assertThat(q.getAnswers().size(), greaterThan(0));
            assertThat(q.getComments().size(), greaterThan(0));

            List<Answer> answers = q.getAnswers();
            List<CommentQuestion> comments = q.getComments();

            int answerSize = answers.size();
            int commentSize = comments.size();

            long[] answerIds = new long[answerSize];
            long[] commentIds = new long[commentSize];

            int answerCommentIdsSize =0;
            for (Answer answer : answers) {
                answerCommentIdsSize += answer.getComments().size();
            }

            long[] answerCommentIds = new long[answerCommentIdsSize];
            int answerCommentIdsIndex = 0;

            for (int i = 0; i < answerSize; i++) {
                answerIds[i] = answers.get(i).getId();
                for (int y = 0; y < answers.get(i).getComments().size(); y++) {
                    List<CommentAnswer> commentAnswers = q.getAnswers().get(i).getComments();
                    answerCommentIds[answerCommentIdsIndex] = commentAnswers.get(y).getId();
                    answerCommentIdsIndex++;
                }
            }

            for (int i = 0; i < commentSize; i++) {
                commentIds[i] = comments.get(i).getId();
            }

            assertThat(answerIds, equalTo(Arrays.stream(answerIds).distinct().toArray()));
            assertThat(commentIds, equalTo(Arrays.stream(commentIds).distinct().toArray()));
            assertThat(answerCommentIds, equalTo(Arrays.stream(answerCommentIds).distinct().toArray()));
        }

        @Test
        void assert_not_found_result_equal_null() {
            assertThat(questionDao.getFullQuestion(123432L), equalTo(null));
        }

        @Test
        void assert_no_null_pointer_exception_question_created_only() {
            questionDaoTestUtil.createQuestion();
            Question q = questionDao.getFullQuestion(1L);
            assertThat(q, notNullValue());
            assertThat(q.getAnswers(), notNullValue());
            assertThat(q.getComments(), notNullValue());
        }

        @Test
        void assert_no_null_pointer_exception_question_answer_created_only() {
            answerDaoTestUtil.createAnswer();
            Question q = questionDao.getFullQuestion(1L);
            assertThat(q, notNullValue());
            assertThat(q.getAnswers(), notNullValue());
            assertThat(q.getComments(), notNullValue());
        }
    }

    @Nested
    class get_question_comments {
        @Test
        void assert_correct_result() {
            questionDaoTestUtil.createQuestionWithComments((int) (QuestionDaoTestUtil.COMMENT_RESULT_SIZE * 1.5));
            for (int i = 0; i < 2; i++) {
                List<CommentQuestion> comments = questionDao.getQuestionComments(1L, i);
                assertThat(comments, notNullValue());
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
            questionDaoTestUtil.createQuestionWithComments((int) (QuestionDaoTestUtil.COMMENT_RESULT_SIZE * 1.5));

            List<CommentQuestion> comments1 = questionDao.getQuestionComments(1L, 0);
            List<CommentQuestion> comments2 = questionDao.getQuestionComments(1L, 1);
            assertThat(comments1, notNullValue());
            assertThat(comments2, notNullValue());

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
            void assert_result_equal_null_question_not_exist() {
                assertThat(questionDao.getQuestionComments(1L, 1), equalTo(null));
            }

            @Test
            void assert_result_equal_empty_list_question_exist() {
                questionDaoTestUtil.createQuestion();
                assertThat(questionDao.getQuestionComments(1L, 1), equalTo(Collections.emptyList()));
            }
        }
    }

    @Nested
    class get_question_answers {

    }

    @Nested
    class get_question_views {
        @Test
        void assert_correct_result() {
            questionDaoTestUtil.createManyQuestionsWithManyAnswers(
                    (int) (QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE * 1.5),
                    QuestionDaoTestUtil.RESULT_SIZE);
            for (int i = 0; i < 2; i++) {
                List<QuestionViewDto> views = questionDao.getQuestionViewsDto(i);
                assertThat(views, notNullValue());
                for (QuestionViewDto v : views) {
                    assertThat(v.getQuestionId(), notNullValue());
                    assertThat(v.getTags(), notNullValue());
                    assertThat(v.getTitle(), notNullValue());
                    assertThat(v.getCreationDate(), notNullValue());
                    assertThat(v.getLastActivity(), notNullValue());
                    assertThat(v.getAuthor(), notNullValue());
                    assertThat(v.getAuthor().getUsername(), notNullValue());
                    assertThat(v.getAnswersCount(), notNullValue());
                }
            }
        }

        @Test
        void assert_no_duplicates() {
            questionDaoTestUtil.createManyQuestionsWithManyAnswers(
                    (int) (QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE * 1.5),
                    QuestionDaoTestUtil.RESULT_SIZE);

            List<QuestionViewDto> dto1 = questionDao.getQuestionViewsDto(0);
            List<QuestionViewDto> dto2 = questionDao.getQuestionViewsDto(1);
            assertThat(dto1, notNullValue());
            assertThat(dto2, notNullValue());
            int size1 = dto1.size();
            int size2 = dto2.size();
            long[] ids1 = new long[size1];
            long[] ids2 = new long[size2];
            for (int i = 0; i < size1; i++) {
                ids1[i] = dto1.get(i).getQuestionId();
            }
            for (int i = 0; i < size2; i++) {
                ids2[i] = dto2.get(i).getQuestionId();
            }
            assertThat(ids1, equalTo(Arrays.stream(ids1).distinct().toArray()));
            assertThat(ids2, equalTo(Arrays.stream(ids2).distinct().toArray()));
        }

        @Test
        void assert_not_found_result_equal_empty_list() {
            List<QuestionViewDto> dto1 = questionDao.getQuestionViewsDto(1231230);
            assertThat(dto1, equalTo(Collections.emptyList()));
        }
    }
}
