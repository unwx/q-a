package qa.rest;

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
import qa.cache.JedisResourceCenter;
import qa.logger.TestLogger;
import qa.security.jwt.service.JwtProvider;
import qa.tools.annotations.SpringIntegrationTest;
import qa.util.dao.AnswerDaoTestUtil;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.dao.query.params.AnswerQueryParameters;
import qa.util.hibernate.HibernateSessionFactoryUtil;
import qa.util.mock.JedisMockTestUtil;
import qa.util.rest.AnswerRestTestUtil;
import qa.util.rest.JwtTestUtil;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringIntegrationTest
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
        JedisResourceCenter jedisResourceCenter = JedisMockTestUtil.mockJedisFactory();
        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
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
}
