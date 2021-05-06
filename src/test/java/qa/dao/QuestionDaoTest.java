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
import qa.cache.like.QuestionLikesProvider;
import qa.dao.util.HibernateSessionFactoryConfigurer;
import qa.domain.*;
import qa.domain.setters.PropertySetterFactory;
import qa.logger.TestLogger;
import qa.tools.annotations.MockitoTest;
import redis.clients.jedis.Jedis;
import util.dao.AnswerDaoTestUtil;
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
public class QuestionDaoTest {

    private QuestionDao questionDao;
    private QuestionDaoTestUtil questionDaoTestUtil;
    private AnswerDaoTestUtil answerDaoTestUtil;

    private SessionFactory sessionFactory;
    private JedisResourceCenter jedisResourceCenter;

    private RedisTestUtil redisTestUtil;

    private final TestLogger logger = new TestLogger(QuestionDaoTest.class);

    private static final String LOG_CORRECT_RESULT              = "assert correct result";
    private static final String LOG_NO_DUPLICATES               = "assert no duplicates";
    private static final String LOG_RESULT_NULL                 = "assert result equals null, when question not exist";
    private static final String LOG_NO_NPE_QUESTION             = "assert no NPE, when question created only";
    private static final String LOG_NO_NPE_QUESTION_ANSWER      = "assert no NPE, when question & answer created only";
    private static final String LOG_RESULT_EXIST                = "assert result exist, when answers not exist";
    private static final String LOG_RESULT_EMPTY_LIST           = "assert not found result equals empty list";
    private static final String LOG_RESULT_MINUS_ONE            = "assert not found result equals -1";
    private static final String LOG_CORRECT_KEYS                = "assert result equals empty list, when question exist";
    private static final String LOG_LIKE_ONE_TIME               = "assert can't like more than one times";
    private static final String LOG_LIKED_STATUS_TRUE           = "assert get user liked status equals true";
    private static final String LOG_LIKED_STATUS_FALSE          = "assert get user liked status equals false";
    private static final String LOG_REMOVES_LINKED_KEYS         = "assert removes all linked keys";
    private static final String LOG_REMOVES_NESTED_KEYS         = "assert removes all nested keys";
    private static final String LOG_NO_EXCEPTIONS               = "assert no exceptions";

    @BeforeAll
    void init() {
        final PropertySetterFactory propertySetterFactory = Mockito.spy(PropertySetterFactory.class);
        final QuestionLikesProvider likesProvider = MockUtil.mockQuestionLikeProvider();

        sessionFactory = HibernateSessionFactoryConfigurer.getSessionFactory();
        jedisResourceCenter = MockUtil.mockJedisCenter();

        questionDao = new QuestionDao(propertySetterFactory, sessionFactory, jedisResourceCenter, likesProvider);
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);
        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
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
    class get_full_question {

        @Test
        void assert_correct_result() {
            logger.trace(LOG_CORRECT_RESULT);

            final long questionId = 1L;
            final long userId = -1L;
            final int answers = 6;
            final int comments = 3;

            questionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(answers, comments);
            final Question q = questionDao.getFullQuestion(questionId, userId);

            assertThat(q, notNullValue());
            assertThat(q.getId(), equalTo(1L));
            assertThat(q.getText(), notNullValue());
            assertThat(q.getTitle(), notNullValue());
            assertThat(q.getCreationDate(), notNullValue());
            assertThat(q.getLastActivity(), notNullValue());
            assertThat(q.getTags(), notNullValue());

            assertThat(q.getLikes(), equalTo(0));
            assertThat(q.isLiked(), equalTo(false));

            assertThat(q.getAuthor(), notNullValue());
            assertThat(q.getAuthor().getUsername(), notNullValue());

            assertThat(q.getAnswers(), notNullValue());
            assertThat(q.getAnswers().isEmpty(), equalTo(false));
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
                assertThat(a.getComments().isEmpty(), equalTo(false));
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
            assertThat(q.getComments().isEmpty(), equalTo(false));
            for (CommentQuestion cq : q.getComments()) {
                assertThat(cq.getId(), notNullValue());
                assertThat(cq.getText(), notNullValue());
                assertThat(cq.getCreationDate(), notNullValue());

                assertThat(cq.getAuthor(), notNullValue());
                assertThat(cq.getAuthor().getUsername(), notNullValue());

                assertThat(cq.getLikes(), equalTo(0));
                assertThat(cq.isLiked(), equalTo(false));
            }
        }

        @Test
        void assert_no_duplicates() {
            logger.trace(LOG_NO_DUPLICATES);

            final long questionId = 1L;
            final long userId = -1L;
            final int answersCount = 6;
            final int commentsCount = 3;

            questionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(answersCount, commentsCount);
            final Question q = questionDao.getFullQuestion(questionId, userId);

            assertThat(q, notNullValue());

            assertThat(q.getAnswers().size(), greaterThan(0));
            assertThat(q.getComments().size(), greaterThan(0));

            final List<Answer> answers = q.getAnswers();
            final List<CommentQuestion> comments = q.getComments();

            final int answerSize = answers.size();
            final int commentSize = comments.size();

            final long[] answerIds = new long[answerSize];
            final long[] commentIds = new long[commentSize];

            int answerCommentIdsSize = 0;
            for (Answer answer : answers) {
                answerCommentIdsSize += answer.getComments().size();
            }

            final long[] answerCommentIds = new long[answerCommentIdsSize];
            int answerCommentIdsIndex = 0;

            for (int i = 0; i < answerSize; i++) {
                answerIds[i] = answers.get(i).getId();
                final List<CommentAnswer> commentAnswers = q.getAnswers().get(i).getComments();
                for (CommentAnswer commentAnswer : commentAnswers) {
                    answerCommentIds[answerCommentIdsIndex] = commentAnswer.getId();
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
            logger.trace(LOG_RESULT_NULL);

            final long questionId = 1L;
            final long userId = -1;

            final Question question = questionDao.getFullQuestion(questionId, userId);
            assertThat(question, equalTo(null));
        }

        @Test
        void assert_no_null_pointer_exception_question_created_only() {
            logger.trace(LOG_NO_NPE_QUESTION);
            questionDaoTestUtil.createQuestion();

            final long questionId = 1L;
            final long userId = -1L;

            final Question q = questionDao.getFullQuestion(questionId, userId);
            assertThat(q, notNullValue());
            assertThat(q.getAnswers(), notNullValue());
            assertThat(q.getComments(), notNullValue());
        }

        @Test
        void assert_no_null_pointer_exception_question_answer_created_only() {
            logger.trace(LOG_NO_NPE_QUESTION_ANSWER);
            answerDaoTestUtil.createAnswer();

            final long questionId = 1L;
            final long userId = -1L;

            final Question q = questionDao.getFullQuestion(questionId, userId);
            assertThat(q, notNullValue());
            assertThat(q.getAnswers(), notNullValue());
            assertThat(q.getComments(), notNullValue());
        }
    }

    @Nested
    class get_question_views {

        @Test
        void assert_correct_result() {
            logger.trace(LOG_CORRECT_RESULT);
            questionDaoTestUtil.createManyQuestionsWithManyAnswers(
                    (QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE),
                    QuestionDaoTestUtil.RESULT_SIZE);

            final int page = 0;

            final List<QuestionView> views = questionDao.getQuestionViewsDto(page);
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
            logger.trace(LOG_NO_DUPLICATES);
            questionDaoTestUtil.createManyQuestionsWithManyAnswers(
                    (int) (QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE * 1.5),
                    QuestionDaoTestUtil.RESULT_SIZE);

            int page = 0;

            final List<QuestionView> dto1 = questionDao.getQuestionViewsDto(page);
            final List<QuestionView> dto2 = questionDao.getQuestionViewsDto(++page);

            assertThat(dto1, notNullValue());
            assertThat(dto2, notNullValue());

            assertThat(dto1.isEmpty(), equalTo(false));
            assertThat(dto2.isEmpty(), equalTo(false));

            final int size1 = dto1.size();
            final int size2 = dto2.size();

            final long[] ids1 = new long[size1];
            final long[] ids2 = new long[size2];

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
            logger.trace(LOG_RESULT_EXIST);
            questionDaoTestUtil.createManyQuestions(QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE);

            final int page = 0;

            final List<QuestionView> dto = questionDao.getQuestionViewsDto(page);
            assertThat(dto.isEmpty(), equalTo(false));
        }

        @Test
        void assert_not_found_result_equal_empty_list() {
            logger.trace(LOG_RESULT_EMPTY_LIST);

            final int page = 0;

            final List<QuestionView> dto = questionDao.getQuestionViewsDto(page);
            assertThat(dto, equalTo(Collections.emptyList()));
        }
    }

    @Nested
    class get_question_by_answer {
        @Test
        void success() {
            logger.trace(LOG_CORRECT_RESULT);
            answerDaoTestUtil.createAnswer();

            final long answerId = 1L;
            final long questionId = 1L;
            final long authorId = questionDao.getQuestionAuthorIdFromAnswer(answerId);

            assertThat(authorId, equalTo(questionId));
        }

        @Test
        void assert_not_found_result_not_null() {
            logger.trace(LOG_RESULT_MINUS_ONE);
            final long answerId = 1L;

            final long authorId = questionDao.getQuestionAuthorIdFromAnswer(answerId);
            assertThat(authorId, equalTo(-1L));
        }
    }

    @Nested
    class like {
        @Test
        void assert_correct_result() {
            logger.trace(LOG_CORRECT_RESULT);
            questionDaoTestUtil.createQuestion();

            final long questionId = 1L;
            final long userId = 1L;
            final int likes = 15;

            final Question result = questionDao.getFullQuestion(questionId, userId);
            assertThat(result, notNullValue());
            assertThat(result.getLikes(), equalTo(0));

            questionDaoTestUtil.like(likes);

            final Question updatedResult = questionDao.getFullQuestion(questionId, userId);
            assertThat(updatedResult, notNullValue());
            assertThat(updatedResult.getLikes(), equalTo(likes));
        }

        @Test
        void assert_correct_keys() {
            logger.trace(LOG_CORRECT_KEYS);

            final long questionId = 0L;
            final long userId = 1L;
            final int questions = 2;
            final int likes = 15;

            questionDaoTestUtil.createManyQuestions(questions);
            questionDaoTestUtil.like(questionId, likes);

            final Question result = questionDao.getFullQuestion(questionId, userId);
            assertThat(result, notNullValue());
            assertThat(result.getLikes(), equalTo(15));
        }

        @Test
        void assert_no_more_than_one() {
            logger.trace(LOG_LIKE_ONE_TIME);
            questionDaoTestUtil.createQuestion();

            final long userId = 1L;
            final long questionId = 1L;

            questionDao.like(userId, questionId);       // 1 - success
            questionDao.like(userId, questionId);       // 2 - ignore

            final Question result = questionDao.getFullQuestion(questionId, userId);

            assertThat(result, notNullValue());
            assertThat(result.getLikes(), equalTo(1));
        }

        @Test
        void assert_liked_by_user_caller() {
            logger.trace(LOG_LIKED_STATUS_TRUE);
            questionDaoTestUtil.createQuestion();

            final long userId = 1L;
            final long questionId = 1L;

            questionDao.like(userId, questionId);
            final Question result = questionDao.getFullQuestion(questionId, userId);

            assertThat(result, notNullValue());
            assertThat(result.isLiked(), equalTo(true));
        }

        @Test
        void assert_not_liked_by_user_caller() {
            logger.trace(LOG_LIKED_STATUS_FALSE);
            questionDaoTestUtil.createQuestion();

            final long anotherUserId = -1L;
            final long userId = 1L;
            final long questionId = 1L;

            questionDao.like(anotherUserId, questionId);
            final Question result = questionDao.getFullQuestion(questionId, userId);

            assertThat(result, notNullValue());
            assertThat(result.isLiked(), equalTo(false));
        }
    }

    @Nested
    class delete {
        @Test
        void assert_removes_linked_keys() {
            logger.trace(LOG_REMOVES_LINKED_KEYS);
            questionDaoTestUtil.createQuestion();

            final long userId = 1L;
            final long questionId = 1L;

            questionDao.like(userId, questionId);
            questionDao.delete(questionId);

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
            logger.trace(LOG_REMOVES_NESTED_KEYS);

            final long questionId = 1L;
            final int answers = 6;
            final int comments = 3;

            questionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(answers, comments);
            questionDao.delete(questionId);

            /* created many nested keys, which will be removed by links between each other */
            final Set<String> keys = redisTestUtil.getAllKeys();
            assertThat(keys.size(), equalTo(0));
        }

        @Test
        void no_keys() {
            logger.trace(LOG_NO_EXCEPTIONS);

            final long questionId = 1L;
            assertDoesNotThrow(() -> questionDao.delete(questionId));

            final Set<String> keys = redisTestUtil.getAllKeys();
            assertThat(keys.size(), equalTo(0));
        }
    }
}
