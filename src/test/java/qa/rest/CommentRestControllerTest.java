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

import java.math.BigInteger;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class CommentRestControllerTest {

    private final SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final static String defaultUserPassword = "ho3kLS4hl2dp-asd";
    private final static String defaultUserUsername = "user471293";
    private final static String defaultUserEmail = "yahoo@yahoo.com";

    private final String text = "thank you! @username";
    private final String secondText = "@username thank you!";

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
            session.createSQLQuery("truncate table answer cascade").executeUpdate();
            session.createSQLQuery("truncate table comment cascade").executeUpdate();
            transaction.commit();
            RestAssured.baseURI = "http://localhost:8080/api/v1/comment/";
            RestAssured.port = 8080;
        }
    }

    @Test
    void createCommentAnswer_Success() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createAnswerDB();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body(commentAnswerCreateJson().toString());

        Response response = request.post("answer/create");
        assertThat(response.getStatusCode(), equalTo(200));
        assertThat(response.getBody().asString().length(), greaterThan(0));

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            BigInteger result = (BigInteger) session.createSQLQuery("select id from comment where text = :a").setParameter("a", text).uniqueResult();
            transaction.commit();
            assertThat(result.longValue(), equalTo(Long.parseLong(response.getBody().asString())));
        }
    }

    @Test
    void createCommentQuestion_Success() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createAnswerDB();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body(commentQuestionCreateJson().toString());

        Response response = request.post("question/create");
        assertThat(response.getStatusCode(), equalTo(200));
        assertThat(response.getBody().asString().length(), greaterThan(0));

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            BigInteger result = (BigInteger) session.createSQLQuery("select id from comment where text = :a").setParameter("a", text).uniqueResult();
            transaction.commit();
            assertThat(result.longValue(), equalTo(Long.parseLong(response.getBody().asString())));
        }
    }

    @Test
    void editCommentAnswer_Success() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createCommentAnswerDB();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body(commentEditJson().toString());

        Response response = request.put("answer/edit");
        assertThat(response.getStatusCode(), equalTo(200));

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            BigInteger result = (BigInteger) session.createSQLQuery("select id from comment where text = :a").setParameter("a", secondText).uniqueResult();
            transaction.commit();
            assertThat(result.longValue(), notNullValue());
        }
    }

    @Test
    void editCommentQuestion_Success() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createCommentQuestionDB();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body(commentEditJson().toString());

        Response response = request.put("question/edit");
        assertThat(response.getStatusCode(), equalTo(200));

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            BigInteger result = (BigInteger) session.createSQLQuery("select id from comment where text = :a").setParameter("a", secondText).uniqueResult();
            transaction.commit();
            assertThat(result.longValue(), notNullValue());
        }
    }

    @Test
    void deleteCommentAnswer_Success() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createCommentAnswerDB();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body("{\"id\":1}");

        Response response = request.delete("answer/delete");
        assertThat(response.getStatusCode(), equalTo(200));

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Object result = session.createSQLQuery("select id from comment where text = :a").setParameter("a", text).uniqueResult();
            transaction.commit();
            assertThat(result, equalTo(null));
        }
    }

    @Test
    void deleteCommentQuestion_Success() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createCommentQuestionDB();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body("{\"id\":1}");

        Response response = request.delete("question/delete");
        assertThat(response.getStatusCode(), equalTo(200));

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Object result = session.createSQLQuery("select id from comment where text = :a").setParameter("a", text).uniqueResult();
            transaction.commit();
            assertThat(result, equalTo(null));
        }
    }

    @Test
    void notRealAuthorCommentAnswer_AccessDenied() {
        JwtData data = jwtProvider.createAccess("second@yahoo.com");
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createSecondUserWithToken(exp);
        createUserWithToken(1L);
        createCommentAnswerDB();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);

        request.body(commentEditJson().toString());

        Response response = request.put("answer/edit");
        assertThat(response.getStatusCode(), equalTo(403));

        request.body("{\"id\":1}");

        Response responseDel = request.delete("answer/delete");
        assertThat(responseDel.getStatusCode(), equalTo(403));
    }

    @Test
    void notRealAuthorCommentQuestion_AccessDenied() {
        JwtData data = jwtProvider.createAccess("second@yahoo.com");
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createSecondUserWithToken(exp);
        createUserWithToken(1L);
        createCommentQuestionDB();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);

        request.body(commentEditJson().toString());

        Response response1 = request.put("question/edit");
        assertThat(response1.getStatusCode(), equalTo(403));

        request.body("{\"id\":1}");

        Response response1Del = request.delete("question/delete");
        assertThat(response1Del.getStatusCode(), equalTo(403));
    }

    private JSONObject commentEditJson() {
        JSONObject json = new JSONObject();
        json.put("id", 1L);
        json.put("text", secondText);
        return json;
    }

    private JSONObject commentAnswerCreateJson() {
        JSONObject json = new JSONObject();
        json.put("answer_id", 1L);
        json.put("text", text);
        return json;
    }

    private JSONObject commentQuestionCreateJson() {
        JSONObject json = new JSONObject();
        json.put("question_id", 1L);
        json.put("text", text);
        return json;
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

    private void createAnswerDB() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("" +
                    "insert into question (id, creation_date, last_activity, tags, text, title, author_id) " +
                    "values (1, '%s', '%s', 'java, etc', 'text dsfdsfdsf', 'title dgfsdf', 1)".formatted(new Date(), new Date())).executeUpdate();
            session.createSQLQuery(
                    "insert into answer (id, answered, creation_date, text, author_id, question_id)" +
                            " values (1, %s, '%s', '%s', 1, 1)".formatted(true, new Date(), text)).executeUpdate();
            transaction.commit();
        }
    }

    private void createQuestionDB() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("" +
                    "insert into question (id, creation_date, last_activity, tags, text, title, author_id) " +
                    "values (1, '%s', '%s', 'java, etc', '%s', '%s', 1)".formatted(new Date(), new Date(), "text", "title")).executeUpdate();
            transaction.commit();
        }
    }

    private void createCommentAnswerDB() {
        createAnswerDB();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("insert into comment (comment_type, id, text, author_id, answer_id, question_id, creation_date) " +
                    "values ('answer', 1, '%s', 1, 1, null, '%s')".formatted(text, new Date())).executeUpdate();
            transaction.commit();
        }
    }

    private void createCommentQuestionDB() {
        createQuestionDB();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("insert into comment (comment_type, id, text, author_id, answer_id, question_id, creation_date) " +
                    "values ('question', 1, '%s', 1, null, 1, '%s')".formatted(text, new Date())).executeUpdate();
            transaction.commit();
        }
    }
}
