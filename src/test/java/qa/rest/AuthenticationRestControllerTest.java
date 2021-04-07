package qa.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
import qa.dto.response.JwtPairResponse;
import qa.exceptions.rest.ErrorMessage;
import qa.security.PasswordEncryptorFactory;
import qa.security.jwt.entity.JwtStatus;
import qa.security.jwt.service.JwtProvider;
import qa.util.dao.UserDaoTestUtil;
import qa.util.hibernate.HibernateSessionFactoryUtil;
import qa.util.rest.AuthenticationRestTestUtil;
import qa.util.rest.JwtTestUtil;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// tomcat server should be launched
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AuthenticationRestControllerTest {

    private SessionFactory sessionFactory;
    private UserDaoTestUtil userDaoTestUtil;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncryptorFactory passwordEncryptorFactory;

    @BeforeAll
    void init() {
        sessionFactory =  HibernateSessionFactoryUtil.getSessionFactory();
        userDaoTestUtil = new UserDaoTestUtil(sessionFactory);
    }

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

    @Nested
    class registration {
        @Test
        void success_assert_valid_tokens() throws JsonProcessingException {
            JSONObject json = AuthenticationRestTestUtil.getRegistrationJson();

            RequestSpecification request = AuthenticationRestTestUtil.getRequestJson(json.toString());

            Response response = request.post("registration");
            assertThat(response.getStatusCode(), equalTo(200));

            String body = response.getBody().asString();
            ObjectMapper mapper = new ObjectMapper();
            JwtPairResponse tokens = mapper.readValue(body, JwtPairResponse.class);

            assertThat(jwtProvider.validate(JwtTestUtil.resolveToken(tokens.getAccess())).getStatus(), equalTo(JwtStatus.VALID));
            assertThat(jwtProvider.validate(JwtTestUtil.resolveToken(tokens.getRefresh())).getStatus(), equalTo(JwtStatus.VALID));
        }

        @Test
        void failed_user_already_exist() throws JsonProcessingException {
            userDaoTestUtil.createUser();

            JSONObject json = AuthenticationRestTestUtil.getRegistrationJson();

            RequestSpecification request = AuthenticationRestTestUtil.getRequestJson(json.toString());

            Response response = request.post("registration");
            assertThat(response.getStatusCode(), equalTo(400));

            String body = response.getBody().asString();
            ObjectMapper mapper = new ObjectMapper();
            ErrorMessage errorMessage = mapper.readValue(body, ErrorMessage.class);

            assertThat(errorMessage.getTimestamp(), notNullValue());
            assertThat(errorMessage.getMessage(), notNullValue());
            assertThat(errorMessage.getStatusCode(), equalTo(400));
        }
    }

    @Nested
    class login {
        @Test
        void success_assert_valid_tokens() throws JsonProcessingException {
            JwtTestUtil.createUserWithRefreshTokenAndEncryptedPassword(sessionFactory, jwtProvider, passwordEncryptorFactory.create());

            JSONObject json = AuthenticationRestTestUtil.getLoginJson();

            RequestSpecification request = AuthenticationRestTestUtil.getRequestJson(json.toString());

            Response response = request.post("login");
            assertThat(response.getStatusCode(), equalTo(200));

            String body = response.getBody().asString();
            ObjectMapper mapper = new ObjectMapper();
            JwtPairResponse tokens = mapper.readValue(body, JwtPairResponse.class);

            assertThat(jwtProvider.validate(JwtTestUtil.resolveToken(tokens.getAccess())).getStatus(), equalTo(JwtStatus.VALID));
            assertThat(jwtProvider.validate(JwtTestUtil.resolveToken(tokens.getRefresh())).getStatus(), equalTo(JwtStatus.VALID));
        }

        @Test
        void failed_wrong_login_or_password() throws JsonProcessingException {
            JSONObject json = AuthenticationRestTestUtil.getLoginJson();

            RequestSpecification request = AuthenticationRestTestUtil.getRequestJson(json.toString());

            Response response = request.post("login");
            assertThat(response.getStatusCode(), equalTo(401));

            String body = response.getBody().asString();
            ObjectMapper mapper = new ObjectMapper();
            ErrorMessage errorMessage = mapper.readValue(body, ErrorMessage.class);

            assertThat(errorMessage.getTimestamp(), notNullValue());
            assertThat(errorMessage.getMessage(), notNullValue());
            assertThat(errorMessage.getStatusCode(), equalTo(401));
        }
    }

    @Nested
    class refresh_tokens {
        @Test
        void success_assert_valid_tokens() throws JsonProcessingException {
            ImmutablePair<String, Long> pair = JwtTestUtil.createUserWithRefreshTokenAndEncryptedPassword(
                    sessionFactory,
                    jwtProvider,
                    passwordEncryptorFactory.create());

            String token = pair.left;
            Long startExp = pair.right;

            RequestSpecification request = AuthenticationRestTestUtil.getRequestJwt(token);

            Response response = request.post("refresh-tokens");
            assertThat(response.getStatusCode(), equalTo(200));

            String body = response.getBody().asString();
            ObjectMapper mapper = new ObjectMapper();
            JwtPairResponse tokens = mapper.readValue(body, JwtPairResponse.class);

            assertThat(jwtProvider.validate(JwtTestUtil.resolveToken(tokens.getAccess())).getStatus(), equalTo(JwtStatus.VALID));
            assertThat(jwtProvider.validate(JwtTestUtil.resolveToken(tokens.getRefresh())).getStatus(), equalTo(JwtStatus.VALID));

            /* user tokenExp should change */
            Long exp = getRefreshExp();
            assertThat(exp, not(startExp));
        }

        @Test
        void failed_invalid_refresh_token() throws JsonProcessingException {
            ImmutablePair<String, Long> pair = JwtTestUtil.createUserWithRefreshTokenAndEncryptedPassword(
                    sessionFactory,
                    jwtProvider,
                    passwordEncryptorFactory.create());

            String invalidToken = pair.left.replaceAll("a", "b");
            Long startExp = pair.right;

            RequestSpecification request = AuthenticationRestTestUtil.getRequestJwt(invalidToken);

            Response response = request.post("refresh-tokens");
            assertThat(response.getStatusCode(), equalTo(401));

            String body = response.getBody().asString();
            ObjectMapper mapper = new ObjectMapper();
            ErrorMessage errorMessage = mapper.readValue(body, ErrorMessage.class);

            assertThat(errorMessage.getTimestamp(), notNullValue());
            assertThat(errorMessage.getMessage(), notNullValue());
            assertThat(errorMessage.getStatusCode(), equalTo(401));

            /* user tokenExp should not change */
            Long exp = getRefreshExp();
            assertThat(exp, equalTo(startExp));
        }
    }

    private Long getRefreshExp() {
        String sql =
                """
                SELECT a.refresh_token_exp_date\s\
                FROM authentication AS a\s\
                WHERE a.id = :id\
                """;
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            BigInteger exp = (BigInteger) session
                    .createSQLQuery(sql)
                    .setParameter("id", 1L)
                    .uniqueResult();
            transaction.commit();
            return exp == null ? null : exp.longValue();
        }
    }
}
