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
import qa.logger.TestLogger;
import qa.security.jwt.service.JwtProvider;
import qa.tools.annotations.SpringIntegrationTest;
import qa.util.dao.AnswerDaoTestUtil;
import qa.util.dao.CommentDaoTestUtil;
import qa.util.dao.query.params.CommentQueryParameters;
import qa.util.hibernate.HibernateSessionFactoryUtil;
import qa.util.rest.CommentRestTestUtil;
import qa.util.rest.JwtTestUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringIntegrationTest
public class CommentAnswerRestControllerTest {

    private SessionFactory sessionFactory;
    private CommentDaoTestUtil commentDaoTestUtil;
    private AnswerDaoTestUtil answerDaoTestUtil;

    @Autowired
    private JwtProvider jwtProvider;

    private final TestLogger logger = new TestLogger(CommentAnswerRestControllerTest.class);

    @BeforeAll
    void init() {
        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
        commentDaoTestUtil = new CommentDaoTestUtil(sessionFactory);
        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory);
        RestAssured.baseURI = "http://localhost:8080/api/v1/comment/answer";
        RestAssured.port = 8080;
    }

    @BeforeEach
    void truncate() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table question cascade").executeUpdate();
            session.createSQLQuery("truncate table question_comment cascade").executeUpdate();
            session.createSQLQuery("truncate table answer_comment cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
            session.createSQLQuery("truncate table answer cascade").executeUpdate();
            session.createSQLQuery("truncate table comment cascade").executeUpdate();
            transaction.commit();
        }
    }

    @Nested
    class success {
        @Test
        void create() {
            logger.trace("create request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            answerDaoTestUtil.createAnswerNoUser();
            JSONObject json = CommentRestTestUtil.commentAnswerCreateJson();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.post("answer/create");
            assertThat(response.getStatusCode(), equalTo(200));

            assertThat(CommentRestTestUtil.getId(CommentQueryParameters.TEXT, sessionFactory), notNullValue());
        }

        @Test
        void edit() {
            logger.trace("edit request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            commentDaoTestUtil.createCommentAnswerNoUser();
            JSONObject json = CommentRestTestUtil.commentEditJson();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.put("answer/edit");
            assertThat(response.getStatusCode(), equalTo(200));

            assertThat(CommentRestTestUtil.getId(CommentQueryParameters.SECOND_TEXT, sessionFactory), notNullValue());
        }

        @Test
        void delete() {
            logger.trace("delete request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            commentDaoTestUtil.createCommentAnswerNoUser();

            JSONObject json = CommentRestTestUtil.id();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.delete("answer/delete");
            assertThat(response.getStatusCode(), equalTo(200));

            assertThat(CommentRestTestUtil.getId(CommentQueryParameters.TEXT, sessionFactory), equalTo(null));
        }
    }

    @Nested
    class bad_request {
        @Test
        void create() {
            logger.trace("create bad request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            JSONObject json = CommentRestTestUtil.commentAnswerBADCreateJson();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.post("answer/create");
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void edit() {
            logger.trace("edit bad request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            JSONObject json = CommentRestTestUtil.commentBADEditJson();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.put("answer/edit");
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void delete() {
            logger.trace("delete bad request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            JSONObject json = CommentRestTestUtil.badId();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.delete("answer/delete");
            assertThat(response.getStatusCode(), equalTo(400));
        }
    }

    @Nested
    class access_denied {
        @Test
        void edit() {
            logger.trace("edit request");
            String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            commentDaoTestUtil.createCommentAnswer();

            JSONObject json = CommentRestTestUtil.commentEditJson();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.put("answer/edit");
            assertThat(response.getStatusCode(), equalTo(403));

            assertThat(CommentRestTestUtil.getId(CommentQueryParameters.SECOND_TEXT, sessionFactory), equalTo(null));
        }

        @Test
        void delete() {
            logger.trace("delete request");
            String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            commentDaoTestUtil.createCommentAnswer();

            JSONObject json = CommentRestTestUtil.id();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.delete("answer/delete");
            assertThat(response.getStatusCode(), equalTo(403));

            assertThat(CommentRestTestUtil.getId(CommentQueryParameters.TEXT, sessionFactory), notNullValue());
        }
    }
}
