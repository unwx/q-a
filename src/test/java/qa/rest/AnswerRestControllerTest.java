package qa.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.cache.RedisKeys;
import qa.domain.CommentAnswer;
import qa.dto.response.answer.AnswerFullResponse;
import qa.logger.TestLogger;
import qa.security.jwt.service.JwtProvider;
import qa.tools.annotations.SpringTest;
import redis.clients.jedis.Jedis;
import util.dao.AnswerDaoTestUtil;
import util.dao.QuestionDaoTestUtil;
import util.dao.TruncateUtil;
import util.dao.query.params.AnswerQueryParameters;
import util.rest.AnswerRestTestUtil;
import util.rest.JwtTestUtil;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringTest
public class AnswerRestControllerTest {

    private QuestionDaoTestUtil questionDaoTestUtil;
    private AnswerDaoTestUtil answerDaoTestUtil;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JedisResourceCenter jedisResourceCenter;

    @Autowired
    private SessionFactory sessionFactory;

    private final TestLogger logger = new TestLogger(AnswerRestControllerTest.class);

    private static final String CREATE_ENDPOINT             = "create";
    private static final String EDIT_ENDPOINT               = "edit";
    private static final String DELETE_ENDPOINT             = "delete";
    private static final String ANSWERED_ENDPOINT           = "answered";
    private static final String NOT_ANSWERED_ENDPOINT       = "not-answered";
    private static final String GET_ENDPOINT                = "get";
    private static final String LIKE_ENDPOINT               = "like";

    private static final String LOG_CREATE                  = "create answer request";
    private static final String LOG_EDIT                    = "edit answer request";
    private static final String LOG_DELETE                  = "delete answer request";
    private static final String LOG_ANSWERED                = "answered request";
    private static final String LOG_NOT_ANSWERED            = "not answered request";
    private static final String LOG_GET_URL                 = "get answers by url only";
    private static final String LOG_GET_JSON                = "get answers by json only";
    private static final String LOG_LIKED                   = "assert liked";
    private static final String LOG_BAD_REQUEST             = "assert bad request";

    @BeforeAll
    void init() {
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);
        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
        RestAssured.baseURI = "http://localhost:8080/api/v1/answer/";
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
    class success {

        @Test
        void create() {
            logger.trace(LOG_CREATE);

            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            questionDaoTestUtil.createQuestionNoUser();

            final JSONObject json = AnswerRestTestUtil.createAnswerJson();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(CREATE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            final Long createdId = getId(AnswerQueryParameters.TEXT);
            final Long responseId = Long.parseLong(response.getBody().asString());

            assertThat(createdId, equalTo(responseId));
        }

        @Test
        void edit() {
            logger.trace(LOG_EDIT);

            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            answerDaoTestUtil.createAnswerNoUser();

            final JSONObject json = AnswerRestTestUtil.editAnswerJson();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.put(EDIT_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            final Long editedId = getId(AnswerQueryParameters.SECOND_TEXT);
            assertThat(editedId, notNullValue());
        }

        @Test
        void delete() {
            logger.trace(LOG_DELETE);

            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            answerDaoTestUtil.createAnswerNoUser();

            final Long createdId = getId(AnswerQueryParameters.TEXT);
            assertThat(createdId, notNullValue());

            final JSONObject json = AnswerRestTestUtil.id();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.delete(DELETE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            final Long deletedId = getId(AnswerQueryParameters.TEXT);
            assertThat(deletedId, equalTo(null));
        }

        @Test
        void answered() {
            logger.trace(LOG_ANSWERED);

            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            answerDaoTestUtil.createAnswerNoUser();

            final JSONObject json = AnswerRestTestUtil.id();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(ANSWERED_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            assertThat(getAnswered(), equalTo(true));
        }

        @Test
        void not_answered() {
            logger.trace(LOG_NOT_ANSWERED);

            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            answerDaoTestUtil.createAnswerNoUser(false);

            final JSONObject json = AnswerRestTestUtil.id();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(NOT_ANSWERED_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            assertThat(getAnswered(), equalTo(false));
        }
    }

    @Nested
    class bad_request {

        @Test
        void create() {
            logger.trace(LOG_CREATE);
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            final JSONObject json = AnswerRestTestUtil.createBADAnswerJson();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(CREATE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void edit() {
            logger.trace(LOG_EDIT);
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            final JSONObject json = AnswerRestTestUtil.editBADAnswerJson();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.put(EDIT_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void answered() {
            logger.trace(LOG_ANSWERED);
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            final JSONObject json = AnswerRestTestUtil.badId();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(ANSWERED_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void not_answered() {
            logger.trace(NOT_ANSWERED_ENDPOINT);
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            final JSONObject json = AnswerRestTestUtil.badId();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(NOT_ANSWERED_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void delete() {
            logger.trace(LOG_DELETE);
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            final JSONObject json = AnswerRestTestUtil.badId();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.delete(DELETE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(400));
        }
    }

    @Nested
    class access_denied {

        @Test
        void edit() {
            logger.trace(EDIT_ENDPOINT);

            final String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            answerDaoTestUtil.createAnswerNoUser();

            final JSONObject json = AnswerRestTestUtil.editAnswerJson();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.put(EDIT_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(403));
        }

        @Test
        void answered() {
            logger.trace(LOG_ANSWERED);

            final String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            answerDaoTestUtil.createAnswerNoUser();

            final JSONObject json = AnswerRestTestUtil.id();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(ANSWERED_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(403));
        }

        @Test
        void not_answered() {
            logger.trace(LOG_NOT_ANSWERED);

            final String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            answerDaoTestUtil.createAnswerNoUser(false);

            final JSONObject json = AnswerRestTestUtil.id();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(NOT_ANSWERED_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(403));
        }

        @Test
        void delete() {
            logger.trace(LOG_DELETE);

            final String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            answerDaoTestUtil.createAnswerNoUser();

            final JSONObject json = AnswerRestTestUtil.id();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.delete(DELETE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(403));
        }
    }

    @Nested
    class get_answers {
        @Nested
        class success {
            @Test
            void url() throws JsonProcessingException {
                logger.trace(LOG_GET_URL);

                final int answers = 9;
                final int comments = 3;
                final String path = GET_ENDPOINT + "/1/1";

                answerDaoTestUtil.createManyAnswersWithManyComments(answers, comments);
                final RequestSpecification request = AnswerRestTestUtil.getRequest();

                final Response response = request.get(path);
                assertCorrectAnswerData(response);
            }

            @Test
            void json() throws JsonProcessingException {
                logger.trace(LOG_GET_JSON);

                final int answers = 9;
                final int comments = 3;

                answerDaoTestUtil.createManyAnswersWithManyComments(answers, comments);
                final JSONObject json = AnswerRestTestUtil.getAnswerJson();
                final RequestSpecification request = AnswerRestTestUtil.getRequestJson(json.toString());

                final Response response = request.get(GET_ENDPOINT);
                assertCorrectAnswerData(response);
            }
        }

        @Nested
        class bad_request {
            @Test
            void url() {
                logger.trace(LOG_GET_URL);
                final RequestSpecification request = AnswerRestTestUtil.getRequest();

                final String path = GET_ENDPOINT + "/1/0";

                final Response response = request.get(path);
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void json() {
                logger.trace(LOG_GET_JSON);
                final JSONObject json = AnswerRestTestUtil.badGetAnswerJson();
                final RequestSpecification request = AnswerRestTestUtil.getRequestJson(json.toString());

                final Response response = request.get(GET_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(400));
            }
        }
    }

    @Nested
    class like {
        @Test
        void assert_liked() {
            logger.trace(LOG_LIKED);
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            answerDaoTestUtil.createAnswerNoUser();

            final JSONObject json = AnswerRestTestUtil.id();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(LIKE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));
            assertKeysUpdated();
        }

        @Test
        void bad_request() {
            logger.trace(LOG_BAD_REQUEST);
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            final JSONObject json = AnswerRestTestUtil.badId();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(LIKE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(400));
        }
    }

    private void assertCorrectAnswerData(Response response) throws JsonProcessingException {
        final ObjectMapper mapper = AnswerRestTestUtil.getObjectMapper();
        final String body = response.body().asString();

        final AnswerFullResponse[] answers = mapper.readValue(body, AnswerFullResponse[].class);
        for (final AnswerFullResponse answer : answers) {
            assertThat(answer, notNullValue());
            assertThat(answer.getAnswerId(), notNullValue());
            assertThat(answer.getText(), notNullValue());
            assertThat(answer.getAnswered(), notNullValue());
            assertThat(answer.getCreationDate(), notNullValue());

            assertThat(answer.getAuthor(), notNullValue());
            assertThat(answer.getAuthor().getUsername(), notNullValue());

            assertThat(answer.getLikes(), equalTo(0));
            assertThat(answer.isLiked(), equalTo(false));
            for (CommentAnswer comment : answer.getComments()) {
                assertThat(comment.getId(), notNullValue());
                assertThat(comment.getText(), notNullValue());

                assertThat(comment.getAuthor(), notNullValue());
                assertThat(comment.getAuthor().getUsername(), notNullValue());

                assertThat(comment.getLikes(), equalTo(0));
                assertThat(comment.isLiked(), equalTo(false));
            }
        }
    }

    private void assertKeysUpdated() {
        final String key = "1";
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            final boolean userEntityCreated = jedis.sadd(RedisKeys.getUserToAnswerLikes(key), "1") == 0;
            final boolean entityUserCreated = jedis.sadd(RedisKeys.getAnswerToUserLikes(key), "1") == 0;
            final boolean entityUpdated = jedis.get(RedisKeys.getAnswerLikes(key)).equals("1");
            assertThat(userEntityCreated && entityUserCreated && entityUpdated, equalTo(true));
        }
    }

    private Long getId(String text) {
        final String sql = "SELECT id FROM answer WHERE text = :text";
        final BigInteger result;

        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            result = (BigInteger) session
                    .createSQLQuery(sql)
                    .setParameter("text", text)
                    .uniqueResult();

            transaction.commit();
        }
        return result == null ? null : result.longValue();
    }

    private Boolean getAnswered() {
        final String sql = "SELECT answered FROM answer WHERE text = :text";
        final Boolean result;

        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            result = (Boolean) session
                    .createSQLQuery(sql)
                    .setParameter("text", AnswerQueryParameters.TEXT)
                    .uniqueResult();

            transaction.commit();
        }
        return result;
    }
}
