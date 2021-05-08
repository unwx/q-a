package qa.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.cache.RedisKeys;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.CommentQuestion;
import qa.dto.response.question.QuestionFullResponse;
import qa.dto.response.question.QuestionViewResponse;
import qa.logger.TestLogger;
import qa.security.jwt.service.JwtProvider;
import qa.tools.annotations.SpringTest;
import redis.clients.jedis.Jedis;
import util.dao.QuestionDaoTestUtil;
import util.dao.TruncateUtil;
import util.dao.query.params.QuestionQueryParameters;
import util.rest.JwtTestUtil;
import util.rest.QuestionRestTestUtil;

import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringTest
public class QuestionRestControllerTest {

    private QuestionDaoTestUtil questionDaoTestUtil;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JedisResourceCenter jedisResourceCenter;

    @Autowired
    private SessionFactory sessionFactory;

    private final TestLogger logger = new TestLogger(QuestionRestControllerTest.class);

    private static final String CREATE_ENDPOINT             = "create";
    private static final String EDIT_ENDPOINT               = "edit";
    private static final String DELETE_ENDPOINT             = "delete";
    private static final String GET_VIEWS_ENDPOINT          = "get/views";
    private static final String GET_FULL_ENDPOINT           = "get/full";
    private static final String LIKE_ENDPOINT               = "like";

    private static final String LOG_CREATE                  = "create question request";
    private static final String LOG_EDIT                    = "edit question request";
    private static final String LOG_DELETE                  = "delete question request";
    private static final String LOG_GET_VIEWS_URL           = "get views by url only";
    private static final String LOG_GET_VIEWS_JSON          = "get views by json only";
    private static final String LOG_GET_FULL_URL            = "get question by url only";
    private static final String LOG_GET_FULL_JSON           = "get question by json only";
    private static final String LOG_LIKED                   = "assert liked";
    private static final String LOG_BAD_REQUEST             = "assert bad request";

    @BeforeAll
    void init() {
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);

        RestAssured.baseURI = "http://localhost:8080/api/v1/question/";
        RestAssured.port = 8080;
    }

    @BeforeEach
    void truncate() {
        try (Session session = sessionFactory.openSession()) {
            TruncateUtil.truncatePQ(session);
        }
        try (JedisResource resource = jedisResourceCenter.getResource()) {
            final Jedis jedis = resource.getJedis();
            TruncateUtil.truncateRedis(jedis);
        }
    }

    @Nested
    class CUD {

        @Nested
        class success {

            @Test
            void create() {
                logger.trace(LOG_CREATE);
                final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
                final RequestSpecification request = QuestionRestTestUtil.createQuestionRequest(token);

                final Response response = request.post(CREATE_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(200));

                final Long responseId = Long.parseLong(response.body().asString());
                final Long createdId = getId(QuestionQueryParameters.TEXT);
                assertThat(responseId, equalTo(createdId));
            }

            @Test
            void edit() {
                logger.trace(LOG_EDIT);
                final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
                final RequestSpecification request = QuestionRestTestUtil.editQuestionRequest(token);
                questionDaoTestUtil.createQuestionNoUser();

                final Response response = request.put(EDIT_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(200));

                final Long editedId = getId(QuestionQueryParameters.SECOND_TEXT);
                assertThat(editedId, notNullValue());
            }

            @Test
            void delete() {
                logger.trace(LOG_DELETE);
                final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
                final RequestSpecification request = QuestionRestTestUtil.idRequest(token);
                questionDaoTestUtil.createQuestionNoUser();

                final Response response = request.delete(DELETE_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(200));

                final Long deletedId = getId(QuestionQueryParameters.TEXT);
                assertThat(deletedId, equalTo(null));
            }
        }

        @Nested
        class bad_request {

            @Test
            void create() {
                logger.trace(LOG_CREATE);
                final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
                final RequestSpecification request = QuestionRestTestUtil.createQuestionBadRequest(token);

                final Response response = request.post(CREATE_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void edit() {
                logger.trace(LOG_EDIT);
                final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
                final RequestSpecification request = QuestionRestTestUtil.editQuestionBadRequest(token);

                final Response response = request.put(EDIT_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void delete() {
                logger.trace(LOG_DELETE);
                final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
                final RequestSpecification request = QuestionRestTestUtil.idBadRequest(token);

                Response response = request.delete(DELETE_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(400));
            }
        }

        @Nested
        class access_denied {

            @Test
            void edit() {
                logger.trace(LOG_EDIT);
                final String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
                final RequestSpecification request = QuestionRestTestUtil.editQuestionRequest(token);
                questionDaoTestUtil.createQuestion();

                final Response response = request.put(EDIT_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(403));

                final Long questionId = getId(QuestionQueryParameters.SECOND_TEXT);
                assertThat(questionId, equalTo(null));
            }

            @Test
            void delete() {
                logger.trace(LOG_DELETE);
                final String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
                final RequestSpecification request = QuestionRestTestUtil.idRequest(token);
                questionDaoTestUtil.createQuestion();

                final Response response = request.delete(DELETE_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(403));

                final Long questionId = getId(QuestionQueryParameters.SECOND_TEXT);
                assertThat(questionId, equalTo(null));
            }
        }
    }

    @Nested
    class get {

        @Nested
        class question_views {

            @Nested
            class success {

                @Test
                void json() throws JsonProcessingException {
                    logger.trace(LOG_GET_VIEWS_JSON);
                    questionDaoTestUtil.createManyQuestions(QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE);
                    final RequestSpecification request = QuestionRestTestUtil.pageRequest();

                    final Response response = request.get(GET_VIEWS_ENDPOINT);
                    assertThat(response.getStatusCode(), equalTo(200));

                    assertCorrectDataQuestionViews(response.getBody().asString());
                }

                @Test
                void url() throws JsonProcessingException {
                    logger.trace(LOG_GET_VIEWS_URL);
                    questionDaoTestUtil.createManyQuestions(QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE);

                    final String path = GET_VIEWS_ENDPOINT + "/1";
                    final RequestSpecification request = QuestionRestTestUtil.getRequest();

                    final Response response = request.get(path);
                    assertThat(response.getStatusCode(), equalTo(200));

                    assertCorrectDataQuestionViews(response.getBody().asString());
                }
            }

            @Nested
            class bad_request {

                @Test
                void json() {
                    logger.trace(LOG_GET_VIEWS_JSON);
                    questionDaoTestUtil.createManyQuestions(QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE);
                    final RequestSpecification request = QuestionRestTestUtil.pageBadRequest();

                    final Response response = request.get(GET_VIEWS_ENDPOINT);
                    assertThat(response.getStatusCode(), equalTo(400));
                }

                @Test
                void url() {
                    logger.trace("by url");
                    questionDaoTestUtil.createManyQuestions(QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE);

                    final String path = GET_VIEWS_ENDPOINT + "/-1";
                    final RequestSpecification request = QuestionRestTestUtil.getRequest();

                    final Response response = request.get(path);
                    assertThat(response.getStatusCode(), equalTo(400));
                }
            }
        }

        @Nested
        class full_question {

            @Nested
            class success {

                @Test
                void json() throws JsonProcessingException {
                    logger.trace(LOG_GET_FULL_JSON);
                    questionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(
                            QuestionDaoTestUtil.RESULT_SIZE,
                            QuestionDaoTestUtil.COMMENT_RESULT_SIZE);

                    final RequestSpecification request = QuestionRestTestUtil.idRequest();

                    final Response response = request.get(GET_FULL_ENDPOINT);
                    assertThat(response.getStatusCode(), equalTo(200));

                    assertCorrectDataFullQuestion(response.getBody().asString());
                }

                @Test
                void url() throws JsonProcessingException {
                    logger.trace(LOG_GET_FULL_URL);
                    questionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(
                            QuestionDaoTestUtil.RESULT_SIZE,
                            QuestionDaoTestUtil.COMMENT_RESULT_SIZE);

                    final String path = GET_FULL_ENDPOINT + "/1";
                    final RequestSpecification request = QuestionRestTestUtil.getRequest();

                    final Response response = request.get(path);
                    assertThat(response.getStatusCode(), equalTo(200));

                    assertCorrectDataFullQuestion(response.getBody().asString());
                }
            }

            @Nested
            class bad_request {

                @Test
                void json() {
                    logger.trace(LOG_GET_FULL_JSON);
                    final RequestSpecification request = QuestionRestTestUtil.idBadRequest();

                    final Response response = request.get(GET_FULL_ENDPOINT);
                    assertThat(response.getStatusCode(), equalTo(400));
                }

                @Test
                void url() {
                    logger.trace(LOG_GET_FULL_URL);
                    final String path = GET_FULL_ENDPOINT + "/-1";
                    final RequestSpecification request = QuestionRestTestUtil.getRequest();

                    final Response response = request.get(path);
                    assertThat(response.getStatusCode(), equalTo(400));
                }
            }
        }
    }

    @Nested
    class like {
        @Test
        void assert_liked() {
            logger.trace(LOG_LIKED);
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            final RequestSpecification request = QuestionRestTestUtil.idRequest(token);
            questionDaoTestUtil.createQuestionNoUser();

            final Response response = request.post(LIKE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            assertKeysUpdated();
        }

        @Test
        void bad_request() {
            logger.trace(LOG_BAD_REQUEST);
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            final RequestSpecification request = QuestionRestTestUtil.idBadRequest(token);

            final Response response = request.post(LIKE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(400));
        }
    }

    private Long getId(String text) {
        final String sql = "SELECT id FROM question WHERE text = :text";
        final BigInteger result;

        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            result = (BigInteger) session.createSQLQuery(sql)
                    .setParameter("text", text)
                    .uniqueResult();

            transaction.commit();
        }
        return result == null ? null : result.longValue();
    }

    private void assertCorrectDataQuestionViews(String json) throws JsonProcessingException {
        final ObjectMapper mapper = QuestionRestTestUtil.getObjectMapper();
        final QuestionViewResponse[] views = mapper.readValue(json, QuestionViewResponse[].class);
        assertThat(views.length, greaterThan(0));

        for (QuestionViewResponse q : views) {
            assertThat(q, notNullValue());
            assertThat(q.getQuestionId(), notNullValue());
            assertThat(q.getTitle(), notNullValue());
            assertThat(q.getAnswersCount(), notNullValue());
            assertThat(q.getCreationDate(), notNullValue());
            assertThat(q.getLastActivity(), notNullValue());
            assertThat(q.getTags(), notNullValue());
            assertThat(q.getUser(), notNullValue());
            assertThat(q.getUser().getUsername(), notNullValue());
        }
    }

    private void assertCorrectDataFullQuestion(String json) throws JsonProcessingException {
        final ObjectMapper mapper = QuestionRestTestUtil.getObjectMapper();
        final QuestionFullResponse question = mapper.readValue(json, QuestionFullResponse.class);

        final List<CommentQuestion> commentQuestions = question.getComments();
        final List<Answer> answers = question.getAnswers();

        assertThat(question, notNullValue());
        assertThat(question.getQuestionId(), notNullValue());
        assertThat(question.getTitle(), notNullValue());
        assertThat(question.getText(), notNullValue());
        assertThat(question.getCreationDate(), notNullValue());
        assertThat(question.getLastActivity(), notNullValue());
        assertThat(question.getTags(), notNullValue());
        assertThat(question.getTags().length, greaterThan(0));
        assertThat(question.getLikes(), equalTo(0));
        assertThat(question.isLiked(), equalTo(false));

        assertThat(question.getAuthor(), notNullValue());
        assertThat(question.getAuthor().getUsername(), notNullValue());

        for (CommentQuestion c : commentQuestions) {
            assertThat(c, notNullValue());
            assertThat(c.getId(), notNullValue());
            assertThat(c.getText(), notNullValue());
            assertThat(c.getCreationDate(), notNullValue());
            assertThat(c.getAuthor(), notNullValue());
            assertThat(c.getAuthor().getUsername(), notNullValue());
            assertThat(c.getLikes(), equalTo(0));
            assertThat(c.isLiked(), equalTo(false));
        }

        for (Answer a : answers) {
            assertThat(a, notNullValue());
            assertThat(a.getId(), notNullValue());
            assertThat(a.getText(), notNullValue());
            assertThat(a.getCreationDate(), notNullValue());
            assertThat(a.getAnswered(), notNullValue());
            assertThat(a.getAuthor(), notNullValue());
            assertThat(a.getAuthor().getUsername(), notNullValue());
            assertThat(a.getLikes(), equalTo(0));
            assertThat(a.isLiked(), equalTo(false));
            for (CommentAnswer ca : a.getComments()) {
                assertThat(ca, notNullValue());
                assertThat(ca.getId(), notNullValue());
                assertThat(ca.getText(), notNullValue());
                assertThat(ca.getCreationDate(), notNullValue());
                assertThat(ca.getAuthor(), notNullValue());
                assertThat(ca.getAuthor().getUsername(), notNullValue());
                assertThat(ca.getLikes(), equalTo(0));
                assertThat(ca.isLiked(), equalTo(false));
            }
        }
    }

    private void assertKeysUpdated() {
        final String key = "1";
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            final boolean userEntityCreated = jedis.sadd(RedisKeys.getUserToQuestionLikes(key), "1") == 0;
            final boolean entityUserCreated = jedis.sadd(RedisKeys.getQuestionToUserLikes(key), "1") == 0;
            final boolean entityUpdated = jedis.get(RedisKeys.getQuestionLikes(key)).equals("1");
            assertThat(userEntityCreated && entityUserCreated && entityUpdated, equalTo(true));
        }
    }
}
