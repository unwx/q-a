package qa.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import qa.TestLogger;
import qa.config.spring.SpringConfig;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.CommentQuestion;
import qa.dto.response.question.QuestionFullResponse;
import qa.dto.response.question.QuestionViewResponse;
import qa.security.jwt.service.JwtProvider;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.dao.query.params.QuestionQueryParameters;
import qa.util.hibernate.HibernateSessionFactoryUtil;
import qa.util.rest.JwtTestUtil;
import qa.util.rest.QuestionRestTestUtil;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class QuestionRestControllerTest {

    private SessionFactory sessionFactory;

    @Autowired
    private JwtProvider jwtProvider;

    private QuestionDaoTestUtil questionDaoTestUtil;

    private static final Logger logger = LogManager.getLogger(QuestionRestControllerTest.class);

    @BeforeAll
    void init() {
        TestLogger.info(logger, "init", 3);
        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory);
    }

    @BeforeEach
    void truncate() {
        TestLogger.info(logger, "truncate", 3);
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table question cascade").executeUpdate();
            session.createSQLQuery("truncate table question_comment cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
            transaction.commit();
            RestAssured.baseURI = "http://localhost:8080/api/v1/question/";
            RestAssured.port = 8080;
        }
    }

    @Nested
    class CUD {
        @Nested
        class success {
            @Test
            void create() {
                TestLogger.trace(logger, "CUD -> success -> create", 3);
                String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
                JSONObject json = QuestionRestTestUtil.createQuestionJson();
                RequestSpecification request = QuestionRestTestUtil.getRequestJsonJwt(json.toString(), token);

                Response response = request.post("create");
                assertThat(response.getStatusCode(), equalTo(200));

                String body = response.getBody().asString();
                assertThat(body.length(), greaterThan(0));

                assertThat(getId(QuestionQueryParameters.TEXT), notNullValue());
            }

            @Test
            void edit() {
                TestLogger.trace(logger, "CUD -> success -> edit", 3);
                String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
                questionDaoTestUtil.createQuestionNoUser();
                JSONObject json = QuestionRestTestUtil.editQuestionJson();

                RequestSpecification request = QuestionRestTestUtil.getRequestJsonJwt(json.toString(), token);

                Response response = request.put("edit");
                assertThat(response.getStatusCode(), equalTo(200));

                assertThat(getId(QuestionQueryParameters.SECOND_TEXT), notNullValue());
            }

            @Test
            void delete() {
                TestLogger.trace(logger, "CUD -> success -> delete", 3);
                String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
                questionDaoTestUtil.createQuestionNoUser();
                JSONObject json = QuestionRestTestUtil.id();
                RequestSpecification request = QuestionRestTestUtil.getRequestJsonJwt(json.toString(), token);

                Response response = request.delete("delete");
                assertThat(response.getStatusCode(), equalTo(200));

                assertThat(getId(QuestionQueryParameters.TEXT), equalTo(null));
            }
        }

        @Nested
        class bad_request {
            @Test
            void create() {
                TestLogger.trace(logger, "CUD -> bad request -> create", 3);
                String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
                JSONObject json = QuestionRestTestUtil.createBADQuestionJson();
                RequestSpecification request = QuestionRestTestUtil.getRequestJsonJwt(json.toString(), token);

                Response response = request.post("create");
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void edit() {
                TestLogger.trace(logger, "CUD -> bad request -> edit", 3);
                String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
                JSONObject json = QuestionRestTestUtil.editBADQuestionJson();
                RequestSpecification request = QuestionRestTestUtil.getRequestJsonJwt(json.toString(), token);

                Response response = request.put("edit");
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void delete() {
                TestLogger.trace(logger, "CUD -> bad request -> delete", 3);
                String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
                JSONObject json = QuestionRestTestUtil.badId();
                RequestSpecification request = QuestionRestTestUtil.getRequestJsonJwt(json.toString(), token);

                Response response = request.delete("delete");
                assertThat(response.getStatusCode(), equalTo(400));
            }
        }

        @Nested
        class access_denied {
            @Test
            void edit() {
                TestLogger.trace(logger, "CUD -> access denied -> edit", 3);
                String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
                questionDaoTestUtil.createQuestion();
                JSONObject json = QuestionRestTestUtil.editQuestionJson();
                RequestSpecification request = QuestionRestTestUtil.getRequestJsonJwt(json.toString(), token);

                Response response = request.put("edit");
                assertThat(response.getStatusCode(), equalTo(403));
                assertThat(getId(QuestionQueryParameters.SECOND_TEXT), equalTo(null));
            }

            @Test
            void delete() {
                TestLogger.trace(logger, "CUD -> access denied -> delete", 3);
                String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
                questionDaoTestUtil.createQuestion();
                JSONObject json = QuestionRestTestUtil.id();
                RequestSpecification request = QuestionRestTestUtil.getRequestJsonJwt(json.toString(), token);

                Response response = request.delete("delete");
                assertThat(response.getStatusCode(), equalTo(403));
                assertThat(getId(QuestionQueryParameters.SECOND_TEXT), equalTo(null));
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
                    TestLogger.trace(logger, "get -> question views -> success -> json", 3);
                    questionDaoTestUtil.createManyQuestions(QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE);
                    JSONObject json = QuestionRestTestUtil.page();
                    RequestSpecification request = QuestionRestTestUtil.getRequestJson(json.toString());

                    Response response = request.get("get/views");
                    assertThat(response.getStatusCode(), equalTo(200));

                    assertCorrectDataQuestionViews(response.getBody().asString());
                }

                @Test
                void url() throws JsonProcessingException {
                    TestLogger.trace(logger, "get -> question views -> success -> url", 3);
                    questionDaoTestUtil.createManyQuestions(QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE);
                    RequestSpecification request = QuestionRestTestUtil.getRequest();

                    Response response = request.get("get/views/1");
                    assertThat(response.getStatusCode(), equalTo(200));

                    assertCorrectDataQuestionViews(response.getBody().asString());
                }
            }

            @Nested
            class bad_request {
                @Test
                void json() {
                    TestLogger.trace(logger, "get -> question views -> bad request -> json", 3);
                    questionDaoTestUtil.createManyQuestions(QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE);
                    JSONObject json = QuestionRestTestUtil.badPage();
                    RequestSpecification request = QuestionRestTestUtil.getRequestJson(json.toString());

                    Response response = request.get("get/views");
                    assertThat(response.getStatusCode(), equalTo(400));
                }

                @Test
                void url() {
                    TestLogger.trace(logger, "get -> question views -> bad request -> url", 3);
                    questionDaoTestUtil.createManyQuestions(QuestionDaoTestUtil.QUESTION_VIEW_RESULT_SIZE);
                    RequestSpecification request = QuestionRestTestUtil.getRequest();

                    Response response = request.get("get/views/-1");
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
                    TestLogger.trace(logger, "get -> full question -> success -> json", 3);
                    questionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(
                            QuestionDaoTestUtil.RESULT_SIZE,
                            QuestionDaoTestUtil.COMMENT_RESULT_SIZE);
                    JSONObject json = QuestionRestTestUtil.id();
                    RequestSpecification request = QuestionRestTestUtil.getRequestJson(json.toString());

                    Response response = request.get("get/full");
                    assertThat(response.getStatusCode(), equalTo(200));

                    assertCorrectDataFullQuestion(response.getBody().asString());
                }

                @Test
                void url() throws JsonProcessingException {
                    TestLogger.trace(logger, "get -> full question -> success -> url", 3);
                    questionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(
                            QuestionDaoTestUtil.RESULT_SIZE,
                            QuestionDaoTestUtil.COMMENT_RESULT_SIZE);
                    RequestSpecification request = QuestionRestTestUtil.getRequest();

                    Response response = request.get("get/full/1");
                    assertThat(response.getStatusCode(), equalTo(200));

                    assertCorrectDataFullQuestion(response.getBody().asString());
                }
            }

            @Nested
            class bad_request {
                @Test
                void json() {
                    TestLogger.trace(logger, "get -> full question -> bad request -> json", 3);
                    JSONObject json = QuestionRestTestUtil.badId();
                    RequestSpecification request = QuestionRestTestUtil.getRequestJson(json.toString());

                    Response response = request.get("get/full");
                    assertThat(response.getStatusCode(), equalTo(400));
                }

                @Test
                void url() {
                    TestLogger.trace(logger, "get -> full question -> bad request -> url", 3);
                    RequestSpecification request = QuestionRestTestUtil.getRequest();

                    Response response = request.get("get/full/-1");
                    assertThat(response.getStatusCode(), equalTo(400));
                }
            }
        }
    }

    private Long getId(String text) {
        String sql = "SELECT id FROM question WHERE text = :text";
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            BigInteger result = (BigInteger) session.createSQLQuery(sql)
                    .setParameter("text", text)
                    .uniqueResult();
            transaction.commit();
            return result == null ? null : result.longValue();
        }
    }

    private void assertCorrectDataQuestionViews(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        QuestionViewResponse[] views = mapper.readValue(json, QuestionViewResponse[].class);
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
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        QuestionFullResponse question = mapper.readValue(json, QuestionFullResponse.class);

        List<CommentQuestion> commentQuestions = question.getComments();
        List<Answer> answers = question.getAnswers();

        assertThat(question, notNullValue());
        assertThat(question.getQuestionId(), notNullValue());
        assertThat(question.getTitle(), notNullValue());
        assertThat(question.getText(), notNullValue());
        assertThat(question.getCreationDate(), notNullValue());
        assertThat(question.getLastActivity(), notNullValue());
        assertThat(question.getTags(), notNullValue());
        assertThat(question.getTags().length, greaterThan(0));

        assertThat(question.getAuthor(), notNullValue());
        assertThat(question.getAuthor().getUsername(), notNullValue());

        for (CommentQuestion c : commentQuestions) {
            assertThat(c, notNullValue());
            assertThat(c.getId(), notNullValue());
            assertThat(c.getText(), notNullValue());
            assertThat(c.getCreationDate(), notNullValue());
            assertThat(c.getAuthor(), notNullValue());
            assertThat(c.getAuthor().getUsername(), notNullValue());
        }

        for (Answer a : answers) {
            assertThat(a, notNullValue());
            assertThat(a.getId(), notNullValue());
            assertThat(a.getText(), notNullValue());
            assertThat(a.getCreationDate(), notNullValue());
            assertThat(a.getAnswered(), notNullValue());
            assertThat(a.getAuthor(), notNullValue());
            assertThat(a.getAuthor().getUsername(), notNullValue());
            for (CommentAnswer ca : a.getComments()) {
                assertThat(ca, notNullValue());
                assertThat(ca.getId(), notNullValue());
                assertThat(ca.getText(), notNullValue());
                assertThat(ca.getCreationDate(), notNullValue());
                assertThat(ca.getAuthor(), notNullValue());
                assertThat(ca.getAuthor().getUsername(), notNullValue());
            }
        }
    }
}
