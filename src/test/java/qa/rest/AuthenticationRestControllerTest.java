package qa.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import qa.config.spring.SpringConfig;
import qa.dto.response.JwtPairResponse;
import qa.exceptions.rest.ErrorMessage;
import qa.security.PasswordEncryptorFactory;
import qa.security.jwt.entity.JwtData;
import qa.security.jwt.service.JwtProvider;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// tomcat server should be launched
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class AuthenticationRestControllerTest {

    private final SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final static String defaultUserPassword = "ho3kLS4hl2dp-asd";
    private final static String defaultUserUsername = "user471293";
    private final static String defaultUserEmail = "yahoo@yahoo.com";

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncryptorFactory PasswordEncryptorFactory;

    @BeforeEach
    void truncate() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table user_role cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            transaction.commit();
            RestAssured.baseURI = "http://localhost:8080/api/v1/authentication/";
            RestAssured.port = 8080;
        }
    }

    @Test
    void registrationSuccess() throws JsonProcessingException {
        JSONObject requestParams = registrationJson();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(requestParams.toString());

        Response response = request.post("registration");
        assertThat(response.getStatusCode(), equalTo(200));

        String body = response.getBody().asString();
        ObjectMapper mapper = new ObjectMapper();
        JwtPairResponse tokens = mapper.readValue(body, JwtPairResponse.class);

        assertThat(tokens.getAccess().length(), greaterThan(25));
        assertThat(tokens.getRefresh().length(), greaterThan(25));
    }

    @Test
    void registrationFailed_userAlreadyExist() throws JsonProcessingException {
        createUser();

        JSONObject requestParams = registrationJson();
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(requestParams.toString());

        Response response = request.post("registration");
        assertThat(response.getStatusCode(), equalTo(400));

        String body = response.getBody().asString();
        ObjectMapper mapper = new ObjectMapper();
        ErrorMessage errorMessage = mapper.readValue(body, ErrorMessage.class);

        assertThat(errorMessage.getTimestamp(), notNullValue());
        assertThat(errorMessage.getMessage(), notNullValue());
        assertThat(errorMessage.getStatusCode(), equalTo(400));
    }

    @Test
    void loginSuccess() throws JsonProcessingException {
        createUser();

        JSONObject requestParams = loginJson();
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(requestParams.toString());

        Response response = request.post("login");
        assertThat(response.getStatusCode(), equalTo(200));

        String body = response.getBody().asString();
        ObjectMapper mapper = new ObjectMapper();
        JwtPairResponse tokens = mapper.readValue(body, JwtPairResponse.class);

        assertThat(tokens.getAccess().length(), greaterThan(25));
        assertThat(tokens.getRefresh().length(), greaterThan(25));
    }

    @Test
    void loginFailed_wrongLoginOrPassword() throws JsonProcessingException {
        JSONObject requestParams = loginJson();
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(requestParams.toString());

        Response response = request.post("login");
        assertThat(response.getStatusCode(), equalTo(401));

        String body = response.getBody().asString();
        ObjectMapper mapper = new ObjectMapper();
        ErrorMessage errorMessage = mapper.readValue(body, ErrorMessage.class);

        assertThat(errorMessage.getTimestamp(), notNullValue());
        assertThat(errorMessage.getMessage(), notNullValue());
        assertThat(errorMessage.getStatusCode(), equalTo(401));
    }

    @Test
    void refreshTokensSuccess() throws JsonProcessingException {
        JwtData refreshTokenData = jwtProvider.createRefresh(defaultUserEmail);
        String token = "Bearer_" + refreshTokenData.getToken();
        long tokenExpiration = refreshTokenData.getExpirationAtMillis();
        createUserWithRefreshToken(tokenExpiration);

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", token);

        Response response = request.post("refresh-tokens");
        assertThat(response.getStatusCode(), equalTo(200));

        String body = response.getBody().asString();
        ObjectMapper mapper = new ObjectMapper();
        JwtPairResponse tokens = mapper.readValue(body, JwtPairResponse.class);

        assertThat(tokens.getAccess().length(), greaterThan(25));
        assertThat(tokens.getRefresh().length(), greaterThan(25));

        /* user tokenExp should change */
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Long exp = (Long) session
                    .createQuery("select a.refreshTokenExpirationDateAtMillis from AuthenticationData a where a.id=:a")
                    .setParameter("a", 1L)
                    .uniqueResult();
            transaction.commit();

            assertThat(exp, not(tokenExpiration));
        }
    }

    private JSONObject registrationJson() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", defaultUserEmail);
        requestParams.put("username", defaultUserUsername);
        requestParams.put("password", defaultUserPassword);
        return requestParams;
    }

    private JSONObject loginJson() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", defaultUserEmail);
        requestParams.put("password", defaultUserPassword);
        return requestParams;
    }

    private void createUser() {
        PooledPBEStringEncryptor encryptor = PasswordEncryptorFactory.create();
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String sqlUser =
                    """
                    insert into usr\s\
                    (id, username)\s\
                    values (1, '%s');\
                    """.formatted(defaultUserUsername);
            String sqlAuthentication =
                    """
                    insert into authentication (id, access_token_exp_date, email, enabled, password, refresh_token_exp_date, user_id)\s\
                    values (1, 1, '%s', true, '%s', 1, 1); \
                    """.formatted(defaultUserEmail, encryptor.encrypt(defaultUserPassword));
            session.createSQLQuery(sqlUser).executeUpdate();
            session.createSQLQuery(sqlAuthentication).executeUpdate();
            transaction.commit();
        }
    }

    private void createUserWithRefreshToken(long tokenExp) {
        PooledPBEStringEncryptor encryptor = PasswordEncryptorFactory.create();
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String sqlUser =
                    """
                    insert into usr\s\
                    (id, username)\s\
                    values (1, '%s');\
                    """.formatted(defaultUserUsername);
            String sqlAuthentication =
                    """
                    insert into authentication (id, access_token_exp_date, email, enabled, password, refresh_token_exp_date, user_id)\s\
                    values (1, 1, '%s', true, '%s', %s, 1); \
                    """.formatted(defaultUserEmail, encryptor.encrypt(defaultUserPassword), tokenExp);
            String sqlRoles =
                    """
                    insert into user_role (auth_id, roles) VALUES (1, 'USER');\
                    """;

            session.createSQLQuery(sqlUser).executeUpdate();
            session.createSQLQuery(sqlAuthentication).executeUpdate();
            session.createSQLQuery(sqlRoles).executeUpdate();
            transaction.commit();
        }
    }
}
