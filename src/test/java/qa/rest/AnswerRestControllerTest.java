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
import qa.util.dao.AnswerDaoTestUtil;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.dao.query.params.AnswerQueryParameters;
import qa.util.hibernate.HibernateSessionFactoryConfigurer;
import qa.util.rest.AnswerRestTestUtil;
import qa.util.rest.JwtTestUtil;
import redis.clients.jedis.Jedis;

import java.math.BigInteger;
import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringTest
public class AnswerRestControllerTest {

    private SessionFactory sessionFactory;
    private QuestionDaoTestUtil questionDaoTestUtil;
    private AnswerDaoTestUtil answerDaoTestUtil;

    private final TestLogger logger = new TestLogger(AnswerRestControllerTest.class);

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JedisResourceCenter jedisResourceCenter;

    @BeforeAll
    void init() {
        sessionFactory = HibernateSessionFactoryConfigurer.getSessionFactory();
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);
        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
    }

    @BeforeEach
    void truncate() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table question cascade").executeUpdate();
            session.createSQLQuery("truncate table question_comment cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
            session.createSQLQuery("truncate table answer cascade").executeUpdate();
            transaction.commit();
            RestAssured.baseURI = "http://localhost:8080/api/v1/answer/";
            RestAssured.port = 8080;
        }
        JedisResource resource = jedisResourceCenter.getResource();
        resource.getJedis().flushDB();
        resource.close();
    }

    @Nested
    class success {

        @Test
        void create() {
            logger.trace("create request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            questionDaoTestUtil.createQuestionNoUser();

            JSONObject json = AnswerRestTestUtil.createAnswerJson();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.post("create");
            assertThat(response.getStatusCode(), equalTo(200));

            assertThat(getId(AnswerQueryParameters.TEXT), equalTo(Long.parseLong(response.getBody().asString())));
        }

        @Test
        void edit() {
            logger.trace("edit request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            answerDaoTestUtil.createAnswerNoUser();

            JSONObject json = AnswerRestTestUtil.editAnswerJson();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.put("edit");
            assertThat(response.getStatusCode(), equalTo(200));

            assertThat(getId(AnswerQueryParameters.SECOND_TEXT), notNullValue());
        }

        @Test
        void delete() {
            logger.trace("delete request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            answerDaoTestUtil.createAnswerNoUser();

            assertThat(getId(AnswerQueryParameters.TEXT), notNullValue());

            JSONObject json = AnswerRestTestUtil.id();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.delete("delete");
            assertThat(response.getStatusCode(), equalTo(200));

            assertThat(getId(AnswerQueryParameters.TEXT), equalTo(null));
        }

        @Test
        void answered() {
            logger.trace("answered request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            answerDaoTestUtil.createAnswerNoUser();

            JSONObject json = AnswerRestTestUtil.id();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.post("answered");
            assertThat(response.getStatusCode(), equalTo(200));

            assertThat(getAnswered(), equalTo(true));
        }

        @Test
        void not_answered() {
            logger.trace("not answered request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            answerDaoTestUtil.createAnswerNoUser(false);

            JSONObject json = AnswerRestTestUtil.id();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.post("not-answered");
            assertThat(response.getStatusCode(), equalTo(200));

            assertThat(getAnswered(), equalTo(false));
        }
    }

    @Nested
    class bad_request {

        @Test
        void create() {
            logger.trace("create request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            JSONObject json = AnswerRestTestUtil.createBADAnswerJson();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.post("create");
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void edit() {
            logger.trace("edit request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            JSONObject json = AnswerRestTestUtil.editBADAnswerJson();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.put("edit");
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void answered() {
            logger.trace("answered request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            JSONObject json = AnswerRestTestUtil.badId();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.post("answered");
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void not_answered() {
            logger.trace("not answered request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            JSONObject json = AnswerRestTestUtil.badId();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.post("not-answered");
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void delete() {
            logger.trace("delete request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            JSONObject json = AnswerRestTestUtil.badId();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.delete("delete");
            assertThat(response.getStatusCode(), equalTo(400));
        }
    }

    @Nested
    class access_denied {

        @Test
        void edit() {
            logger.trace("edit request");
            String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            answerDaoTestUtil.createAnswerNoUser();

            JSONObject json = AnswerRestTestUtil.editAnswerJson();

            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);
            Response response = request.put("edit");
            assertThat(response.getStatusCode(), equalTo(403));
        }

        @Test
        void answered() {
            logger.trace("answered request");
            String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            answerDaoTestUtil.createAnswerNoUser();

            JSONObject json = AnswerRestTestUtil.id();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);
            Response response = request.post("answered");
            assertThat(response.getStatusCode(), equalTo(403));
        }

        @Test
        void not_answered() {
            logger.trace("not answered request");
            String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            answerDaoTestUtil.createAnswerNoUser(false);

            JSONObject json = AnswerRestTestUtil.id();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);
            Response response = request.post("not-answered");
            assertThat(response.getStatusCode(), equalTo(403));
        }

        @Test
        void delete() {
            logger.trace("delete request");
            String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            answerDaoTestUtil.createAnswerNoUser();

            JSONObject json = AnswerRestTestUtil.id();
            RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);
            Response response = request.delete("delete");
            assertThat(response.getStatusCode(), equalTo(403));
        }
    }

    @Nested
    class get_answers {
        @Nested
        class success {
            @Test
            void url() throws JsonProcessingException {
                logger.trace("get answers by url");
                answerDaoTestUtil.createManyAnswersWithManyComments(9, 3);
                final RequestSpecification request = AnswerRestTestUtil.getRequest();

                final Response response = request.get("get/1/1");
                assertCorrectAnswerData(response);
            }

            @Test
            void json() throws JsonProcessingException {
                logger.trace("get answers by json");
                answerDaoTestUtil.createManyAnswersWithManyComments(9, 3);
                final JSONObject json = AnswerRestTestUtil.getAnswerJson();
                final RequestSpecification request = AnswerRestTestUtil.getRequestJson(json.toString());

                final Response response = request.get("get");
                assertCorrectAnswerData(response);
            }
        }

        @Nested
        class bad_request {
            @Test
            void url() {
                logger.trace("get answers by url");
                final RequestSpecification request = AnswerRestTestUtil.getRequest();

                final Response response = request.get("get/1/0");
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void json() {
                logger.trace("get answers by json");
                final JSONObject json = AnswerRestTestUtil.badGetAnswerJson();
                final RequestSpecification request = AnswerRestTestUtil.getRequestJson(json.toString());

                final Response response = request.get("get");
                assertThat(response.getStatusCode(), equalTo(400));
            }
        }
    }

    @Nested
    class like {
        @Test
        void assert_liked() {
            logger.trace("assert liked");
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            answerDaoTestUtil.createAnswerNoUser();

            final JSONObject json = AnswerRestTestUtil.id();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post("like");
            assertThat(response.getStatusCode(), equalTo(200));
            assertKeysUpdated();
        }

        @Test
        void bad_request() {
            logger.trace("bad request");
            final String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);

            final JSONObject json = AnswerRestTestUtil.badId();
            final RequestSpecification request = AnswerRestTestUtil.getRequestJsonJwt(json.toString(), token);

            final Response response = request.post("like");
            assertThat(response.getStatusCode(), equalTo(400));
        }
    }

    private void assertCorrectAnswerData(Response response) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")); // TODO REFACTOR

        final AnswerFullResponse[] answers = mapper.readValue(response.getBody().asString(), AnswerFullResponse[].class);
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

    private Long getId(String text) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            BigInteger result = (BigInteger) session.createSQLQuery("SELECT id FROM answer WHERE text = :text")
                    .setParameter("text", text).uniqueResult();
            transaction.commit();
            return result == null ? null : result.longValue();
        }
    }

    private Boolean getAnswered() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Boolean result = (Boolean) session.createSQLQuery("SELECT answered FROM answer WHERE text = :text")
                    .setParameter("text", AnswerQueryParameters.TEXT).uniqueResult();
            transaction.commit();
            return result;
        }
    }

    private void assertKeysUpdated() {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            final boolean userEntityCreated = jedis.sadd(RedisKeys.getUserToAnswerLikes("1"), "1") == 0;
            final boolean entityUserCreated = jedis.sadd(RedisKeys.getAnswerToUserLikes("1"), "1") == 0;
            final boolean entityUpdated = jedis.get(RedisKeys.getAnswerLikes("1")).equals("1");
            assertThat(userEntityCreated && entityUserCreated && entityUpdated, equalTo(true));
        }
    }
}
