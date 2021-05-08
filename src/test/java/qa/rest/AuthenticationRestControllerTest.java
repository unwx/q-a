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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import qa.config.PasswordEncryptorFactory;
import qa.dto.response.JwtPairResponse;
import qa.exceptions.rest.ErrorMessage;
import qa.logger.TestLogger;
import qa.security.jwt.entity.JwtStatus;
import qa.security.jwt.service.JwtProvider;
import qa.tools.annotations.SpringTest;
import util.dao.TruncateUtil;
import util.dao.UserDaoTestUtil;
import util.rest.AuthenticationRestTestUtil;
import util.rest.JwtTestUtil;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringTest
public class AuthenticationRestControllerTest {

    private UserDaoTestUtil userDaoTestUtil;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncryptorFactory passwordEncryptorFactory;

    @Autowired
    private SessionFactory sessionFactory;

    private final TestLogger logger = new TestLogger(AuthenticationRestControllerTest.class);

    private static final String REGISTRATION_ENDPOINT           = "registration";
    private static final String LOGIN_ENDPOINT                  = "login";
    private static final String REFRESH_ENDPOINT                = "refresh-tokens";

    private static final String LOG_SUCCESS_VALID               = "success request. assert valid response tokens";
    private static final String LOG_FAILED_USER_EXIST           = "failed request. assert user already exist";
    private static final String LOG_FAILED_WRONG_AUTH           = "failed request. wrong login or password";
    private static final String LOG_FAILED_INVALID_TOKEN        = "failed request. invalid request refresh token";

    @BeforeAll
    void init() {
        userDaoTestUtil = new UserDaoTestUtil(sessionFactory);

        RestAssured.baseURI = "http://localhost:8080/api/v1/authentication/";
        RestAssured.port = 8080;
    }

    @BeforeEach
    void truncate() {
        try (Session session = sessionFactory.openSession()) {
            TruncateUtil.truncatePQ(session);
        }
    }

    @Nested
    class registration {

        @Test
        void success_assert_valid_tokens() throws JsonProcessingException {
            logger.trace(LOG_SUCCESS_VALID);
            final RequestSpecification request = AuthenticationRestTestUtil.getRegistrationRequest();

            final Response response = request.post(REGISTRATION_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            final String body = response.getBody().asString();
            final ObjectMapper mapper = AuthenticationRestTestUtil.getObjectMapper();
            final JwtPairResponse tokens = mapper.readValue(body, JwtPairResponse.class);

            assertValidTokens(tokens);
        }

        @Test
        void failed_user_already_exist() throws JsonProcessingException {
            logger.trace(LOG_FAILED_USER_EXIST);
            userDaoTestUtil.createUser();

            final RequestSpecification request = AuthenticationRestTestUtil.getRegistrationRequest();

            final Response response = request.post(REGISTRATION_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(400));

            final String body = response.getBody().asString();
            final ObjectMapper mapper = AuthenticationRestTestUtil.getObjectMapper();
            final ErrorMessage errorMessage = mapper.readValue(body, ErrorMessage.class);

            assertThat(errorMessage.getTimestamp(), notNullValue());
            assertThat(errorMessage.getMessage(), notNullValue());
            assertThat(errorMessage.getStatusCode(), equalTo(400));
        }
    }

    @Nested
    class login {

        @Test
        void success_assert_valid_tokens() throws JsonProcessingException {
            logger.trace(LOG_SUCCESS_VALID);
            JwtTestUtil.createUserWithRefreshTokenAndEncryptedPassword(sessionFactory, jwtProvider, passwordEncryptorFactory.create());

            final RequestSpecification request = AuthenticationRestTestUtil.getLoginRequest();

            final Response response = request.post(LOGIN_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            final String body = response.getBody().asString();
            final ObjectMapper mapper = AuthenticationRestTestUtil.getObjectMapper();
            final JwtPairResponse tokens = mapper.readValue(body, JwtPairResponse.class);

            assertValidTokens(tokens);
        }

        @Test
        void failed_wrong_login_or_password() throws JsonProcessingException {
            logger.trace(LOG_FAILED_WRONG_AUTH);
            final RequestSpecification request = AuthenticationRestTestUtil.getLoginRequest();

            final Response response = request.post(LOGIN_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(401));

            final String body = response.getBody().asString();
            final ObjectMapper mapper = AuthenticationRestTestUtil.getObjectMapper();
            final ErrorMessage errorMessage = mapper.readValue(body, ErrorMessage.class);

            assertThat(errorMessage.getTimestamp(), notNullValue());
            assertThat(errorMessage.getMessage(), notNullValue());
            assertThat(errorMessage.getStatusCode(), equalTo(401));
        }
    }

    @Nested
    class refresh_tokens {

        @Test
        void success_assert_valid_tokens() throws JsonProcessingException {
            logger.trace(LOG_SUCCESS_VALID);
            final ImmutablePair<String, Long> pair = JwtTestUtil.createUserWithRefreshTokenAndEncryptedPassword(
                    sessionFactory,
                    jwtProvider,
                    passwordEncryptorFactory.create()
            );

            final String token = pair.left;
            final Long startExp = pair.right;

            final RequestSpecification request = AuthenticationRestTestUtil.getRequestJwt(token);
            final Response response = request.post(REFRESH_ENDPOINT);
            assertThat(response.getStatusCode(), equalTo(200));

            final String body = response.getBody().asString();
            final ObjectMapper mapper = AuthenticationRestTestUtil.getObjectMapper();
            final JwtPairResponse tokens = mapper.readValue(body, JwtPairResponse.class);

            assertValidTokens(tokens);

            /* user tokenExp should change */
            final Long exp = getRefreshExp();
            assertThat(exp, not(startExp));
        }

        @Test
        void failed_invalid_refresh_token() throws JsonProcessingException {
            logger.trace(LOG_FAILED_INVALID_TOKEN);
            ImmutablePair<String, Long> pair = JwtTestUtil.createUserWithRefreshTokenAndEncryptedPassword(
                    sessionFactory,
                    jwtProvider,
                    passwordEncryptorFactory.create()
            );

            final String invalidToken = pair.left.replaceAll("c", "b");
            final Long startExp = pair.right;

            final RequestSpecification request = AuthenticationRestTestUtil.getRequestJwt(invalidToken);
            final Response response = request.post("refresh-tokens");

            assertThat(response.getStatusCode(), equalTo(401));

            final String body = response.getBody().asString();
            final ObjectMapper mapper = AuthenticationRestTestUtil.getObjectMapper();
            final ErrorMessage errorMessage = mapper.readValue(body, ErrorMessage.class);

            assertThat(errorMessage.getTimestamp(), notNullValue());
            assertThat(errorMessage.getMessage(), notNullValue());
            assertThat(errorMessage.getStatusCode(), equalTo(401));

            /* user tokenExp should not change */
            final Long exp = getRefreshExp();
            assertThat(exp, equalTo(startExp));
        }
    }

    private void assertValidTokens(JwtPairResponse jwtPair) {
        final String cleanAccess = JwtTestUtil.resolveToken(jwtPair.getAccess());
        final String cleanRefresh = JwtTestUtil.resolveToken(jwtPair.getRefresh());

        final JwtStatus accessStatus = jwtProvider.validate(cleanAccess).getStatus();
        final JwtStatus refreshStatus = jwtProvider.validate(cleanRefresh).getStatus();

        assertThat(accessStatus, equalTo(JwtStatus.VALID));
        assertThat(refreshStatus, equalTo(JwtStatus.VALID));
    }

    private Long getRefreshExp() {
        final String sql =
                """
                SELECT a.refresh_token_exp_date\s\
                FROM authentication AS a\s\
                WHERE a.id = :id\
                """;
        final BigInteger exp;

        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            exp = (BigInteger) session
                    .createSQLQuery(sql)
                    .setParameter("id", 1L)
                    .uniqueResult();

            transaction.commit();
        }
        return exp == null ? null : exp.longValue();
    }
}
