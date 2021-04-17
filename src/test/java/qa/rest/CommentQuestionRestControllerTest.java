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
import qa.cache.JedisResourceCenter;
import qa.dto.response.comment.CommentQuestionResponse;
import qa.logger.TestLogger;
import qa.security.jwt.service.JwtProvider;
import qa.tools.annotations.SpringIntegrationTest;
import qa.util.dao.CommentDaoTestUtil;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.dao.query.params.CommentQueryParameters;
import qa.util.hibernate.HibernateSessionFactoryUtil;
import qa.util.rest.CommentRestTestUtil;
import qa.util.rest.JwtTestUtil;

import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringIntegrationTest
public class CommentQuestionRestControllerTest {

    private SessionFactory sessionFactory;
    private CommentDaoTestUtil commentDaoTestUtil;
    private QuestionDaoTestUtil questionDaoTestUtil;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JedisResourceCenter jedisResourceCenter;

    private final TestLogger logger = new TestLogger(CommentQuestionRestControllerTest.class);

    @BeforeAll
    void init() {
        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
        commentDaoTestUtil = new CommentDaoTestUtil(sessionFactory);
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);
        RestAssured.baseURI = "http://localhost:8080/api/v1/comment/question/";
        RestAssured.port = 8080;
    }

    @BeforeEach
    void truncate() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table question cascade").executeUpdate();
            session.createSQLQuery("truncate table question_comment cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
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
            questionDaoTestUtil.createQuestionNoUser();
            JSONObject json = CommentRestTestUtil.commentQuestionCreateJson();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.post("create");
            assertThat(response.getStatusCode(), equalTo(200));

            assertThat(CommentRestTestUtil.getId(CommentQueryParameters.TEXT, sessionFactory), notNullValue());
        }

        @Test
        void edit() {
            logger.trace("edit request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            commentDaoTestUtil.createCommentQuestionNoUser();
            JSONObject json = CommentRestTestUtil.commentEditJson();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.put("edit");
            assertThat(response.getStatusCode(), equalTo(200));

            assertThat(CommentRestTestUtil.getId(CommentQueryParameters.SECOND_TEXT, sessionFactory), notNullValue());
        }

        @Test
        void delete() {
            logger.trace("delete request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            commentDaoTestUtil.createCommentQuestionNoUser();
            JSONObject json = CommentRestTestUtil.id();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.delete("delete");
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
            JSONObject json = CommentRestTestUtil.commentQuestionBADCreateJson();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.post("create");
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void edit() {
            logger.trace("edit bad request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            JSONObject json = CommentRestTestUtil.commentBADEditJson();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.put("edit");
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void delete() {
            logger.trace("delete bad request");
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            JSONObject json = CommentRestTestUtil.badId();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

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
            commentDaoTestUtil.createCommentQuestion();

            JSONObject json = CommentRestTestUtil.commentEditJson();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.put("edit");
            assertThat(response.getStatusCode(), equalTo(403));

            assertThat(CommentRestTestUtil.getId(CommentQueryParameters.SECOND_TEXT, sessionFactory), equalTo(null));
        }

        @Test
        void delete() {
            logger.trace("delete request");
            String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            commentDaoTestUtil.createCommentQuestion();

            JSONObject json = CommentRestTestUtil.id();

            RequestSpecification request = CommentRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.delete("delete");
            assertThat(response.getStatusCode(), equalTo(403));

            assertThat(CommentRestTestUtil.getId(CommentQueryParameters.TEXT, sessionFactory), notNullValue());
        }
    }

    @Nested
    class get {
        @Nested
        class assert_correct_result {
            @Test
            void json() throws JsonProcessingException {
                logger.trace("by json. assert correct result");
                commentDaoTestUtil.createManyCommentQuestions(CommentDaoTestUtil.COMMENT_RESULT_SIZE);
                JSONObject json = CommentRestTestUtil.idPage();

                RequestSpecification request = CommentRestTestUtil.getRequestJson(json.toString());

                Response response = request.get("get");
                assertThat(response.getStatusCode(), equalTo(200));
                ObjectMapper mapper = new ObjectMapper();
                mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

                CommentQuestionResponse[] body = mapper.readValue(response.getBody().asString(), CommentQuestionResponse[].class);
                assertCorrectResultGetComments(body);
            }

            @Test
            void url() throws JsonProcessingException {
                logger.trace("by url. assert correct result");
                commentDaoTestUtil.createManyCommentQuestions(CommentDaoTestUtil.COMMENT_RESULT_SIZE);

                RequestSpecification request = CommentRestTestUtil.getRequest();

                Response response = request.get("get/1/1");
                assertThat(response.getStatusCode(), equalTo(200));
                ObjectMapper mapper = new ObjectMapper();
                mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

                CommentQuestionResponse[] body = mapper.readValue(response.getBody().asString(), CommentQuestionResponse[].class);
                assertCorrectResultGetComments(body);
            }
        }

        @Nested
        class bad_request {
            @Test
            void json() {
                logger.trace("bad request. assert correct result");
                commentDaoTestUtil.createManyCommentQuestions(CommentDaoTestUtil.COMMENT_RESULT_SIZE);
                JSONObject json = CommentRestTestUtil.badIdPage();

                RequestSpecification request = CommentRestTestUtil.getRequestJson(json.toString());

                Response response = request.get("get");
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void url() {
                logger.trace("bad request. assert correct result");
                commentDaoTestUtil.createManyCommentQuestions(CommentDaoTestUtil.COMMENT_RESULT_SIZE);

                RequestSpecification request = CommentRestTestUtil.getRequest();

                Response response = request.get("get/1/0");
                assertThat(response.getStatusCode(), equalTo(400));
            }
        }
    }

    public static void assertCorrectResultGetComments(CommentQuestionResponse[] response) {
        assertThat(response, notNullValue());
        assertThat(response.length, greaterThan(0));
        for (CommentQuestionResponse r : response) {
            assertThat(r, notNullValue());
            assertThat(r.getCommentId(), notNullValue());
            assertThat(r.getText(), notNullValue());
            assertThat(r.getCreationDate(), notNullValue());
            assertThat(r.getAuthor(), notNullValue());
            assertThat(r.getAuthor().getUsername(), notNullValue());
        }
    }
}
