package qa.rest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import qa.config.spring.SpringConfig;
import qa.security.jwt.entity.JwtData;
import qa.security.jwt.service.JwtProvider;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class QuestionRestControllerTest {

    private final SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final static String defaultUserPassword = "ho3kLS4hl2dp-asd";
    private final static String defaultUserUsername = "user471293";
    private final static String defaultUserEmail = "yahoo@yahoo.com";

    @Autowired
    private JwtProvider jwtProvider;

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

    @Test
    void tokenSecurityTest_WithoutToken() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body("{\"test\":123}");
        Response response = request.post("create");
        assertThat(response.getStatusCode(), equalTo(403));

        Response response1 = request.put("edit");
        assertThat(response1.getStatusCode(), equalTo(403));

        Response response2 = request.delete("delete");
        assertThat(response2.getStatusCode(), equalTo(403));
    }

    @Test
    void tokenSecurityTest_InvalidToken() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();

        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer_" + token);

        Response response = request.post("create");
        assertThat(response.getStatusCode(), equalTo(401));
    }


    @Test
    void createQuestionSuccess() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);

        JSONObject json = createQuestionJson();
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body(json.toString());

        Response response = request.post("create");
        assertThat(response.getStatusCode(), equalTo(200));
        String body = response.getBody().asString();
        assertThat(body.length(), greaterThan(0));
    }

    @Test
    void editQuestionSuccess() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createQuestionDB();

        JSONObject json = editQuestionJson();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body(json.toString());

        Response response = request.put("edit");
        assertThat(response.getStatusCode(), equalTo(200));
    }

    @Test
    void deleteQuestionSuccess() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createQuestionDB();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body("{\"id\":1}");

        Response response = request.delete("delete");
        assertThat(response.getStatusCode(), equalTo(200));
    }

    @Test
    void edit_delete_AccessDenied() {
        JwtData data = jwtProvider.createAccess("second@yahoo.com");
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createSecondUserWithToken(exp);
        createUserWithToken(1L);
        createQuestionDB();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body("{\"id\":1}");

        Response response = request.delete("delete");
        assertThat(response.getStatusCode(), equalTo(403));
    }

    private void createQuestionDB() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("" +
                    "insert into question (id, creation_date, last_activity, tags, text, title, author_id) " +
                    "values (1, '%s', '%s', 'java, etc', 'text dsfdsfdsf', 'title dgfsdf', 1)".formatted(new Date(), new Date())).executeUpdate();
            transaction.commit();
        }
    }

    private void createUserWithToken(long tokenExp) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("insert into usr (id, about, username) values (1, null, '%s')".formatted(defaultUserUsername)).executeUpdate();
            session.createSQLQuery(
                    "insert into authentication (id, access_token_exp_date, email, enabled, password, refresh_token_exp_date, user_id)" +
                    " values (1, %s, '%s', true, '%s', 1, 1)".formatted(tokenExp, defaultUserEmail, defaultUserPassword)).executeUpdate();
            session.createSQLQuery("insert into user_role (auth_id, roles) values (1, 'USER')").executeUpdate();
            transaction.commit();
        }
    }

    private void createSecondUserWithToken(long tokenExp) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("insert into usr (id, about, username) values (2, null, '%s')".formatted("user987654231")).executeUpdate();
            session.createSQLQuery(
                    "insert into authentication (id, access_token_exp_date, email, enabled, password, refresh_token_exp_date, user_id)" +
                            " values (2, %s, '%s', true, '%s', 1, 2)".formatted(tokenExp, "second@yahoo.com", defaultUserPassword)).executeUpdate();
            session.createSQLQuery("insert into user_role (auth_id, roles) values (2, 'USER')").executeUpdate();
            transaction.commit();
        }
    }

    private JSONObject createQuestionJson() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("title", "null pointer exception at . . .");
        requestParams.put("text", """
                What are Null Pointer Exceptions (java.lang.NullPointerException) and what causes them?
                What methods/tools can be used to determine the cause so that you stop the exception from causing the program to terminate prematurely?""");
        requestParams.put("tags", new String[]{"null", "java"});
        return requestParams;
    }

    private JSONObject editQuestionJson() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("text", """
                What are Null Pointer Exceptions (java.lang.NullPointerException) and what causes them?
                What methods/tools can be used to determine the cause so that you stop the exception from causing the program to terminate prematurely?""");
        requestParams.put("tags", new String[]{"null", "java"});
        requestParams.put("id", 1);
        return requestParams;
    }
}
