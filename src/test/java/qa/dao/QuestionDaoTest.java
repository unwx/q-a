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
import qa.cache.entity.like.provider.QuestionCacheProvider;
import qa.domain.*;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.MockitoTest;
import qa.util.dao.AnswerDaoTestUtil;
import qa.util.dao.QuestionDaoTestUtil;
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
public class QuestionDaoTest {

    private QuestionDao questionDao;
    private SessionFactory sessionFactory;
    private QuestionDaoTestUtil questionDaoTestUtil;
    private AnswerDaoTestUtil answerDaoTestUtil;
    private RedisTestUtil redisTestUtil;

    private JedisResourceCenter jedisResourceCenter;

    private final TestLogger logger = new TestLogger(QuestionDaoTest.class);

    @BeforeAll
    void init() {
        sessionFactory = HibernateSessionFactoryConfigurer.getSessionFactory();
        jedisResourceCenter = MockUtil.mockJedisCenter();

        final PropertySetterFactory propertySetterFactory = Mockito.mock(PropertySetterFactory.class);
        final CacheRemover cacheRemover = MockUtil.mockCacheRemover();
        final QuestionCacheProvider cacheProvider = MockUtil.mockQuestionCacheProvider();

        questionDao = new QuestionDao(propertySetterFactory, sessionFactory, jedisResourceCenter, cacheRemover, cacheProvider);
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);
        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
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
    class get_full_question {

        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            questionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(6, 3);
            Question q = questionDao.getFullQuestion(1L, -1L);

            assertThat(q, notNullValue());
            assertThat(q.getId(), equalTo(1L));
            assertThat(q.getText(), notNullValue());
            assertThat(q.getTitle(), notNullValue());
            assertThat(q.getCreationDate(), notNullValue());
            assertThat(q.getLastActivity(), notNullValue());
            assertThat(q.getTags(), notNullValue());
            assertThat(q.getLikes(), notNullValue());
            assertThat(q.isLiked(), equalTo(false));

            assertThat(q.getAuthor(), notNullValue());
            assertThat(q.getAuthor().getUsername(), notNullValue());

            assertThat(q.getAnswers(), notNullValue());
            assertThat(q.getAnswers().size(), greaterThan(0));
            for (Answer a : q.getAnswers()) {
                assertThat(a.getId(), notNullValue());
                assertThat(a.getText(), notNullValue());
                assertThat(a.getAnswered(), notNullValue());
                assertThat(a.getCreationDate(), notNullValue());
                assertThat(a.getAuthor(), notNullValue());
                assertThat(a.getAuthor().getUsername(), notNullValue());
                assertThat(a.getLikes(), equalTo(0));
                assertThat(a.isLiked(), equalTo(false));

                assertThat(a.getComments(), notNullValue());
                assertThat(a.getComments().size(), greaterThan(0));
                for (CommentAnswer c : a.getComments()) {
                    assertThat(c.getId(), notNullValue());
                    assertThat(c.getText(), notNullValue());
                    assertThat(c.getCreationDate(), notNullValue());
                    assertThat(c.getAuthor(), notNullValue());
                    assertThat(c.getAuthor().getUsername(), notNullValue());
                    assertThat(c.getLikes(), equalTo(0));
                    assertThat(c.isLiked(), equalTo(false));
                }
            }

            assertThat(q.getComments(), notNullValue());
            assertThat(q.getComments().size(), lessThan(QuestionDaoTestUtil.COMMENT_RESULT_SIZE + 1));
            for (CommentQuestion cq : q.getComments()) {
                assertThat(cq.getAuthor(), notNullValue());
                assertThat(cq.getAuthor().getUsername(), notNullValue());
                assertThat(cq.getId(), notNullValue());
                assertThat(cq.getText(), notNullValue());
                assertThat(cq.getCreationDate(), notNullValue());
                assertThat(cq.getLikes(), equalTo(0));
                assertThat(cq.isLiked(), equalTo(false));
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace("assert no duplicates");
            questionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(6, 3);
            Question q = questionDao.getFullQuestion(1L, -1L);

            assertThat(q, notNullValue());

            assertThat(q.getAnswers().size(), greaterThan(0));
            assertThat(q.getComments().size(), greaterThan(0));

            List<Answer> answers = q.getAnswers();
            List<CommentQuestion> comments = q.getComments();

            int answerSize = answers.size();
            int commentSize = comments.size();

            long[] answerIds = new long[answerSize];
            long[] commentIds = new long[commentSize];

            int answerCommentIdsSize = 0;
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
        void assert_not_found_result_equals_null() {
            logger.trace("assert result equals null when question not exist");
            assertThat(questionDao.getFullQuestion(123432L, -1L), equalTo(null));
        }

        @Test
        void assert_no_null_pointer_exception_question_created_only() {
            logger.trace("assert no NPE when question created only");
            questionDaoTestUtil.createQuestion();
            Question q = questionDao.getFullQuestion(1L, -1L);
            assertThat(q, notNullValue());
            assertThat(q.getAnswers(), notNullValue());
            assertThat(q.getComments(), notNullValue());
        }

        @Test
        void assert_no_null_pointer_exception_question_answer_created_only() {
            logger.trace("assert no NPE when question & answer created only");
            answerDaoTestUtil.createAnswer();
            Question q = questionDao.getFullQuestion(1L, -1L);
            assertThat(q, notNullValue());
            assertThat(q.getAnswers(), notNullValue());
            assertThat(q.getComments(), notNullValue());
        }
    }

    @Nested
    class get_question_views {

        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            questionDaoTestUtil.createManyQuestionsWithManyAnswers(
                    (QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE),
                    QuestionDaoTestUtil.RESULT_SIZE);
            List<QuestionView> views = questionDao.getQuestionViewsDto(0);
            assertThat(views, notNullValue());
            assertThat(views.size(), greaterThan(0));
            for (QuestionView v : views) {
                assertThat(v.getQuestionId(), notNullValue());
                assertThat(v.getTags(), notNullValue());
                assertThat(v.getTitle(), notNullValue());
                assertThat(v.getCreationDate(), notNullValue());
                assertThat(v.getLastActivity(), notNullValue());
                assertThat(v.getAuthor(), notNullValue());
                assertThat(v.getAuthor().getUsername(), notNullValue());
                assertThat(v.getAnswersCount(), notNullValue());
                assertThat(v.getLikes(), equalTo(0));
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace("assert no duplicates");
            questionDaoTestUtil.createManyQuestionsWithManyAnswers(
                    (int) (QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE * 1.5),
                    QuestionDaoTestUtil.RESULT_SIZE);

            List<QuestionView> dto1 = questionDao.getQuestionViewsDto(0);
            List<QuestionView> dto2 = questionDao.getQuestionViewsDto(1);

            assertThat(dto1, notNullValue());
            assertThat(dto2, notNullValue());

            assertThat(dto1.size(), greaterThan(0));
            assertThat(dto2.size(), greaterThan(0));

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
        void assert_exist_if_answers_not_exist() {
            logger.trace("assert result exist when answers not exist");
            questionDaoTestUtil.createManyQuestions(QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE);
            List<QuestionView> dto = questionDao.getQuestionViewsDto(0);
            assertThat(dto.size(), greaterThan(0));
        }

        @Test
        void assert_not_found_result_equal_empty_list() {
            logger.trace("assert not found result equals empty list");
            List<QuestionView> dto = questionDao.getQuestionViewsDto(1231230);
            assertThat(dto, equalTo(Collections.emptyList()));
        }
    }

    @Nested
    class like {
        @Test
        void assert_correct_result() {
            logger.trace("assert correct result");
            questionDaoTestUtil.createQuestion();
            Question result = questionDao.getFullQuestion(1L, -1L);
            assertThat(result, notNullValue());
            assertThat(result.getLikes(), equalTo(0));

            questionDaoTestUtil.like(15);
            Question updatedResult = questionDao.getFullQuestion(1L, -1L);
            assertThat(updatedResult, notNullValue());
            assertThat(updatedResult.getLikes(), equalTo(15));
        }

        @Test
        void assert_correct_keys() {
            logger.trace("assert correct keys");
            questionDaoTestUtil.createManyQuestions(2);
            questionDaoTestUtil.like(0L, 15);

            Question result = questionDao.getFullQuestion(1L, -1L);
            assertThat(result, notNullValue());
            assertThat(result.getLikes(), equalTo(0));
        }

        @Test
        void assert_success() {
            logger.trace("assert success");
            questionDaoTestUtil.createQuestion();
            questionDao.like(1L, 1L);

            final Question result = questionDao.getFullQuestion(1L, -1L);

            assertThat(result, notNullValue());
            assertThat(result.getLikes(), equalTo(1));
        }

        @Test
        void assert_no_more_than_one() {
            logger.trace("assert can't like more than one times");
            questionDaoTestUtil.createQuestion();

            questionDao.like(1L, 1L);
            questionDao.like(1L, 1L);

            final Question result = questionDao.getFullQuestion(1L, -1L);

            assertThat(result, notNullValue());
            assertThat(result.getLikes(), equalTo(1));
        }

        @Test
        void assert_liked_by_user_caller() {
            logger.trace("assert get user liked status equals true");
            questionDaoTestUtil.createQuestion();
            questionDao.like(1L, 1L);

            final Question result = questionDao.getFullQuestion(1L, 1L);

            assertThat(result, notNullValue());
            assertThat(result.isLiked(), equalTo(true));
        }

        @Test
        void assert_not_liked_by_user_caller() {
            logger.trace("assert get user liked status equals false");
            questionDaoTestUtil.createQuestion();
            questionDao.like(-1L, 1L);

            final Question result = questionDao.getFullQuestion(1L, 1L);

            assertThat(result, notNullValue());
            assertThat(result.isLiked(), equalTo(false));
        }
    }

    @Nested
    class delete {
        @Test
        void assert_success() {
            logger.trace("assert success simple situation");
            questionDaoTestUtil.createQuestion();
            questionDao.like(1L, 1L);

            questionDao.delete(1L);

            final Set<String> keys = redisTestUtil.getAllKeys();
            /*
            * 3 keys created: {question-like (id:counter), user-question (id:id), question-user (id:id)}
            * delete question -> delete `question-like` key, delete all associations with user-question , delete question-user key;
            * 1 key remaining
            * if user-question is empty -> no keys remaining
             */
            assertThat(keys.size(), equalTo(0));
        }

        @Test
        void assert_delete_nested_cache() {
            logger.trace("assert delete linked nested cache");
            questionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(6, 3);
            questionDao.delete(1L);

            /* created many nested keys, which will be removed by links between each other */
            final Set<String> keys = redisTestUtil.getAllKeys();
            assertThat(keys.size(), equalTo(0));
        }

        @Test
        void no_keys() {
            logger.trace("assert no keys situation");

            assertDoesNotThrow(() -> questionDao.delete(1L));
            final Set<String> keys = redisTestUtil.getAllKeys();
            assertThat(keys.size(), equalTo(0));
        }
    }
}
