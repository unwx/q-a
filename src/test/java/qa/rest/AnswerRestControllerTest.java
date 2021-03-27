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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class AnswerRestControllerTest {

    private final SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final static String defaultUserPassword = "ho3kLS4hl2dp-asd";
    private final static String defaultUserUsername = "user471293";
    private final static String defaultUserEmail = "yahoo@yahoo.com";

    private final static String text =
                """
                The first line declares a variable named num, but it does not\s\
                actually contain a primitive value yet. Instead, it contains a\s\
                pointer (because the type is Integer which is a reference type)\s\
                Since you have not yet said what to point to, Java sets it to nu\
                """;
    private final static String secondText =
            """
             at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:1074)
                 at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:988)
                 at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:974)
                 at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:919)
                 at com.mysql.jdbc.ConnectionImpl.buildCollationMapping(ConnectionImpl.java:1062)
                 at com.mysql.jdbc.ConnectionImpl.initializePropsFromServer(ConnectionImpl.java:3556)
                 at com.mysql.jdbc.ConnectionImpl.connectOneTryOnly(ConnectionImpl.java:2513)
                 at com.mysql.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:2283)
                 at com.mysql.jdbc.ConnectionImpl.<init>(ConnectionImpl.java:822)
                 at com.mysql.jdbc.JDBC4Connection.<init>(JDBC4Connection.java:47)\
             """;

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
            transaction.commit();
            RestAssured.baseURI = "http://localhost:8080/api/v1/answer/";
            RestAssured.port = 8080;
        }
    }

    @Test
    void createAnswer_Success() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createQuestionDB();

        JSONObject json = createAnswerJson();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body(json.toString());

        Response response = request.post("create");
        assertThat(response.getStatusCode(), equalTo(200));

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            BigInteger result = (BigInteger) session.createSQLQuery("select id from answer where text = :text").setParameter("text", text).uniqueResult();
            transaction.commit();
            assertThat(result.longValue(), equalTo(Long.parseLong(response.getBody().asString())));
        }
    }

    @Test
    void editAnswer_Success() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createAnswerDB(false);

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body(editAnswerJson().toString());

        Response response = request.put("edit");
        assertThat(response.getStatusCode(), equalTo(200));

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Object result = session.createSQLQuery("select id from answer where text = :text").setParameter("text", secondText).uniqueResult();
            transaction.commit();
            assertThat(result, notNullValue());
        }
    }

    @Test
    void deleteAnswer_Success() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createAnswerDB(false);

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Object result = session.createSQLQuery("select id from answer where text = :text").setParameter("text", text).uniqueResult();
            transaction.commit();
            assertThat(result, notNullValue());
        }

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body("{\"id\":1}");

        Response response = request.delete("delete");
        assertThat(response.getStatusCode(), equalTo(200));

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Object result = session.createSQLQuery("select id from answer where text = :text").setParameter("text", text).uniqueResult();
            transaction.commit();
            assertThat(result, equalTo(null));
        }
    }

    @Test
    void setAnswered_Success() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createAnswerDB(false);

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body("{\"id\":1}");

        Response response = request.post("answered");
        assertThat(response.getStatusCode(), equalTo(200));

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Boolean result = (Boolean) session.createSQLQuery("select answered from answer where text = :text").setParameter("text", text).uniqueResult();
            transaction.commit();
            assertThat(result, equalTo(true));
        }
    }

    @Test
    void removeAnswered_Success() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createUserWithToken(exp);
        createAnswerDB(true);

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body("{\"id\":1}");

        Response response = request.post("not-answered");
        assertThat(response.getStatusCode(), equalTo(200));

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Boolean result = (Boolean) session.createSQLQuery("select answered from answer where text = :text").setParameter("text", text).uniqueResult();
            transaction.commit();
            assertThat(result, equalTo(false));
        }
    }

    @Test
    void notRealAuthor_AccessDenied() {
        JwtData data = jwtProvider.createAccess("second@yahoo.com");
        String token = data.getToken();
        long exp = data.getExpirationAtMillis();
        createSecondUserWithToken(exp);
        createUserWithToken(1L);
        createAnswerDB(false);

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body(editAnswerJson().toString());

        Response response = request.put("edit");
        assertThat(response.getStatusCode(), equalTo(403));

        request.body("{\"id\":1}");

        Response response1Del = request.delete("delete");
        assertThat(response1Del.getStatusCode(), equalTo(403));

        Response response2Answered = request.post("answered");
        assertThat(response2Answered.getStatusCode(), equalTo(403));

        Response response3NotAnswered = request.post("not-answered");
        assertThat(response3NotAnswered.getStatusCode(), equalTo(403));
    }

    private JSONObject createAnswerJson() {
        JSONObject json = new JSONObject();
        json.put("question_id", 1L);
        json.put("text", text);
        return json;
    }

    private JSONObject editAnswerJson() {
        JSONObject json = new JSONObject();
        json.put("id", 1L);
        json.put("text", secondText);
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

    private void createAnswerDB(Boolean answered) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("" +
                    "insert into question (id, creation_date, last_activity, tags, text, title, author_id) " +
                    "values (1, '%s', '%s', 'java, etc', 'text dsfdsfdsf', 'title dgfsdf', 1)".formatted(new Date(), new Date())).executeUpdate();
            session.createSQLQuery(
                    "insert into answer (id, answered, creation_date, text, author_id, question_id)" +
                    " values (1, %s, '%s', '%s', 1, 1)".formatted(answered, new Date(), text)).executeUpdate();
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
}
