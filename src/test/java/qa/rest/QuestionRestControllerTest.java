package qa.rest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
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
import qa.config.spring.SpringConfig;
import qa.security.jwt.service.JwtProvider;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.dao.query.params.QuestionQueryParameters;
import qa.util.hibernate.HibernateSessionFactoryUtil;
import qa.util.rest.JwtTestUtil;
import qa.util.rest.QuestionRestTestUtil;

import java.math.BigInteger;

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

    @BeforeAll
    void init() {
        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory);
    }

    @BeforeEach
    void truncate() {
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
    class success {
        @Test
        void create() {
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
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            JSONObject json = QuestionRestTestUtil.createBADQuestionJson();
            RequestSpecification request = QuestionRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.post("create");
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void edit() {
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            questionDaoTestUtil.createQuestionNoUser();
            JSONObject json = QuestionRestTestUtil.editBADQuestionJson();
            RequestSpecification request = QuestionRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.put("edit");
            assertThat(response.getStatusCode(), equalTo(400));
        }

        @Test
        void delete() {
            String token = JwtTestUtil.createUserWithToken(sessionFactory, jwtProvider);
            questionDaoTestUtil.createQuestionNoUser();
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
            String token = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);
            questionDaoTestUtil.createQuestion();
            JSONObject json = QuestionRestTestUtil.id();
            RequestSpecification request = QuestionRestTestUtil.getRequestJsonJwt(json.toString(), token);

            Response response = request.delete("delete");
            assertThat(response.getStatusCode(), equalTo(403));
            assertThat(getId(QuestionQueryParameters.SECOND_TEXT), equalTo(null));
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
}
