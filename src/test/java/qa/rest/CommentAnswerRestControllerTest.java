package qa.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.cache.RedisKeys;
import qa.dto.response.comment.CommentAnswerResponse;
import qa.logger.TestLogger;
import qa.security.jwt.service.JwtProvider;
import qa.tools.annotations.SpringTest;
import redis.clients.jedis.Jedis;
import util.dao.AnswerDaoTestUtil;
import util.dao.CommentDaoTestUtil;
import util.dao.TruncateUtil;
import util.dao.query.params.CommentQueryParameters;
import util.rest.CommentRestTestUtil;
import util.rest.JwtTestUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringTest
public class CommentAnswerRestControllerTest {

    private CommentDaoTestUtil commentDaoTestUtil;
    private AnswerDaoTestUtil answerDaoTestUtil;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private JedisResourceCenter jedisResourceCenter;

    private final TestLogger logger = new TestLogger(CommentAnswerRestControllerTest.class);

    private static final String CREATE_ENDPOINT          = "create";
    private static final String EDIT_ENDPOINT            = "edit";
    private static final String DELETE_ENDPOINT          = "delete";
    private static final String GET_ENDPOINT             = "get";
    private static final String LIKE_ENDPOINT            = "like";

    private static final String LOG_CREATE               = "create comment-answer request";
    private static final String LOG_EDIT                 = "edit comment-answer request";
    private static final String LOG_DELETE               = "delete comment-answer request";
    private static final String LOG_GET_JSON             = "by json | assert correct result";
    private static final String LOG_GET_URL              = "by url | assert correct result";
    private static final String LOG_LIKED                = "assert liked";
    private static final String LOG_BAD_REQUEST          = "assert bad request";


    @BeforeAll
    void init() {
        commentDaoTestUtil = new CommentDaoTestUtil(sessionFactory, jedisResourceCenter);
        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
        RestAssured.baseURI = "http://localhost:8080/api/v1/comment/answer";
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
            answerDaoTestUtil.createAnswerNoUser();

            final JSONObject json = CommentRestTestUtil.commentAnswerCreateJson();
            final RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(CREATE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            final Long createdId = CommentRestTestUtil.getId(CommentQueryParameters.TEXT, sessionFactory);
            assertThat(createdId, notNullValue());
        }

        @Test
        void edit() {
            logger.trace(EDIT_ENDPOINT);
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            commentDaoTestUtil.createCommentAnswerNoUser();

            final JSONObject json = CommentRestTestUtil.commentEditJson();
            final RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.put(EDIT_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            final Long editedId = CommentRestTestUtil.getId(CommentQueryParameters.SECOND_TEXT, sessionFactory);
            assertThat(editedId, notNullValue());
        }

        @Test
        void delete() {
            logger.trace(LOG_DELETE);
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            commentDaoTestUtil.createCommentAnswerNoUser();

            final JSONObject json = CommentRestTestUtil.id();
            final RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.delete(DELETE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            final Long deletedId = CommentRestTestUtil.getId(CommentQueryParameters.TEXT, sessionFactory);
            assertThat(deletedId, equalTo(null));
        }
    }

    @Nested
    class bad_request {
        @Test
        void create() {
            logger.trace(LOG_CREATE);

            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            final JSONObject json = CommentRestTestUtil.commentAnswerBADCreateJson();
            final RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(CREATE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void edit() {
            logger.trace(LOG_EDIT);

            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            final JSONObject json = CommentRestTestUtil.commentBADEditJson();
            final RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.put(EDIT_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void delete() {
            logger.trace(DELETE_ENDPOINT);

            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            final JSONObject json = CommentRestTestUtil.badId();
            final RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.delete(DELETE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(400));
        }
    }

    @Nested
    class access_denied {
        @Test
        void edit() {
            logger.trace(LOG_EDIT);
            final String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            commentDaoTestUtil.createCommentAnswer();

            final JSONObject json = CommentRestTestUtil.commentEditJson();
            final RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.put(EDIT_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(403));

            final Long commentId = CommentRestTestUtil.getId(CommentQueryParameters.SECOND_TEXT, sessionFactory);
            assertThat(commentId, equalTo(null));
        }

        @Test
        void delete() {
            logger.trace(LOG_DELETE);
            final String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            commentDaoTestUtil.createCommentAnswer();

            final JSONObject json = CommentRestTestUtil.id();
            final RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.delete(DELETE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(403));

            final Long commentId = CommentRestTestUtil.getId(CommentQueryParameters.TEXT, sessionFactory);
            assertThat(commentId, notNullValue());
        }
    }

    @Nested
    class get {
        @Nested
        class assert_correct_result {
            @Test
            void json() throws JsonProcessingException {
                logger.trace(LOG_GET_JSON);
                commentDaoTestUtil.createManyCommentAnswers(CommentDaoTestUtil.COMMENT_RESULT_SIZE);

                final JSONObject json = CommentRestTestUtil.idPage();
                final RequestSpecification request = CommentRestTestUtil.getRequestJson(json.toString());

                final Response response = request.get(GET_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(200));

                final ObjectMapper mapper = CommentRestTestUtil.getObjectMapper();
                final CommentAnswerResponse[] body = mapper.readValue(response.getBody().asString(), CommentAnswerResponse[].class);

                assertCorrectResultGetComments(body);
            }

            @Test
            void url() throws JsonProcessingException {
                logger.trace(LOG_GET_URL);
                commentDaoTestUtil.createManyCommentAnswers(CommentDaoTestUtil.COMMENT_RESULT_SIZE);

                final String path = GET_ENDPOINT + "/1/1";
                final RequestSpecification request = CommentRestTestUtil.getRequest();

                final Response response = request.get(path);
                assertThat(response.getStatusCode(), equalTo(200));

                final ObjectMapper mapper = CommentRestTestUtil.getObjectMapper();
                final CommentAnswerResponse[] body = mapper.readValue(response.getBody().asString(), CommentAnswerResponse[].class);

                assertCorrectResultGetComments(body);
            }
        }

        @Nested
        class bad_request {
            @Test
            void json() {
                logger.trace(LOG_GET_JSON);
                commentDaoTestUtil.createManyCommentAnswers(CommentDaoTestUtil.COMMENT_RESULT_SIZE);

                final JSONObject json = CommentRestTestUtil.badIdPage();
                final RequestSpecification request = CommentRestTestUtil.getRequestJson(json.toString());

                final Response response = request.get(GET_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void url() {
                logger.trace(LOG_GET_JSON);
                commentDaoTestUtil.createManyCommentAnswers(CommentDaoTestUtil.COMMENT_RESULT_SIZE);

                final String path = GET_ENDPOINT + "/1/0";
                final RequestSpecification request = CommentRestTestUtil.getRequest();

                final Response response = request.get(path);
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
            commentDaoTestUtil.createCommentAnswerNoUser();

            final JSONObject json = CommentRestTestUtil.id();
            final RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(LIKE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            assertKeysUpdated();
        }

        @Test
        void bad_request() {
            logger.trace(LOG_BAD_REQUEST);
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            final JSONObject json = CommentRestTestUtil.badId();
            final RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post(LIKE_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(400));
        }
    }

    private void assertCorrectResultGetComments(CommentAnswerResponse[] response) {
        assertThat(response, notNullValue());
        assertThat(response.length, greaterThan(0));
        for (CommentAnswerResponse r : response) {
            assertThat(r, notNullValue());
            assertThat(r.getCommentId(), notNullValue());
            assertThat(r.getText(), notNullValue());
            assertThat(r.getCreationDate(), notNullValue());

            assertThat(r.getAuthor(), notNullValue());
            assertThat(r.getAuthor().getUsername(), notNullValue());

            assertThat(r.getLikes(), equalTo(0));
            assertThat(r.isLiked(), equalTo(false));
        }
    }

    private void assertKeysUpdated() {
        final String key = "1";
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            final boolean userEntityCreated = jedis.sadd(RedisKeys.getUserToCommentAnswerLikes(key), "1") == 0;
            final boolean entityUserCreated = jedis.sadd(RedisKeys.getCommentAnswerToUserLikes(key), "1") == 0;
            final boolean entityUpdated = jedis.get(RedisKeys.getCommentAnswerLikes(key)).equals("1");
            assertThat(userEntityCreated && entityUserCreated && entityUpdated, equalTo(true));
        }
    }
}
