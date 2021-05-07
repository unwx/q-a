package qa.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.domain.Answer;
import qa.domain.Question;
import qa.dto.response.user.UserAnswersResponse;
import qa.dto.response.user.UserFullResponse;
import qa.dto.response.user.UserQuestionsResponse;
import qa.logger.TestLogger;
import qa.tools.annotations.SpringTest;
import redis.clients.jedis.Jedis;
import util.dao.AnswerDaoTestUtil;
import util.dao.QuestionDaoTestUtil;
import util.dao.TruncateUtil;
import util.dao.UserDaoTestUtil;
import util.dao.query.params.UserQueryParameters;
import util.rest.UserRestTestUtil;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringTest
public class UserRestControllerTest {

    private UserDaoTestUtil userDaoTestUtil;
    private AnswerDaoTestUtil answerDaoTestUtil;
    private QuestionDaoTestUtil questionDaoTestUtil;

    @Autowired
    private JedisResourceCenter jedisResourceCenter;

    @Autowired
    private SessionFactory sessionFactory;

    private final TestLogger logger = new TestLogger(UserRestControllerTest.class);

    private static final String GET_ENDPOINT                      = "get";
    private static final String GET_QUESTIONS_ENDPOINT            = "questions/get";
    private static final String GET_ANSWERS_ENDPOINT              = "answers/get";

    private static final String LOG_GET_JSON                      = "by json | assert correct result";
    private static final String LOG_GET_URL                       = "by url | assert correct result";
    private static final String LOG_GET_JSON_EMPTY_ARRAY          = "by json | assert user questions & answers are empty list";
    private static final String LOG_GET_URL_EMPTY_ARRAY           = "by url | assert user questions & answers are empty list";
    private static final String LOG_GET_QUESTIONS_JSON            = "get user questions by json | assert correct result";
    private static final String LOG_GET_QUESTIONS_URL             = "get user questions by url | assert user questions & answers are empty list";
    private static final String LOG_GET_QUESTIONS_EMPTY_LIST      = "get user questions | assert user questions equals empty list";
    private static final String LOG_GET_ANSWERS_JSON              = "get user answers by json | assert user questions & answers are empty list";
    private static final String LOG_GET_ANSWERS_URL               = "get user by url | assert user questions & answers are empty list";
    private static final String LOG_GET_ANSWERS_EMPTY_LIST        = "get user answers by url | assert user answers equals empty list";

    @BeforeAll
    void init() {
        userDaoTestUtil = new UserDaoTestUtil(sessionFactory);
        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);

        RestAssured.baseURI = "http://localhost:8080/api/v1/user/";
        RestAssured.port = 8080;
    }

    @BeforeEach
    void truncate() {
        try (Session session = sessionFactory.openSession()) {
            TruncateUtil.truncatePQ(session);
        }
        try (JedisResource resource = jedisResourceCenter.getResource()) {
            final Jedis jedis = resource.getJedis();
            TruncateUtil.truncateRedis(jedis);
        }
    }


    @Nested
    class get_user {

        @Nested
        class success {

            @Test
            void url() throws JsonProcessingException {
                logger.trace(LOG_GET_URL);

                final int answers = 2;
                questionDaoTestUtil.createManyQuestionsWithManyAnswers(UserDaoTestUtil.RESULT_SIZE, answers);

                final String path = GET_ENDPOINT + '/' + UserQueryParameters.USERNAME;
                final RequestSpecification request = UserRestTestUtil.getRequest();

                final Response response = request.get(path);
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetUserSuccess(response.getBody().asString());
            }

            @Test
            void json() throws JsonProcessingException {
                logger.trace(LOG_GET_JSON);

                final int answers = 2;
                questionDaoTestUtil.createManyQuestionsWithManyAnswers(UserDaoTestUtil.RESULT_SIZE, answers);

                final JSONObject json = UserRestTestUtil.usernameJson();
                final RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                final Response response = request.get(GET_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetUserSuccess(response.getBody().asString());
            }
        }

        @Nested
        class not_found_nested {

            @Test
            void json_assert_empty_list() throws JsonProcessingException {
                logger.trace(LOG_GET_JSON_EMPTY_ARRAY);
                userDaoTestUtil.createUser();

                final JSONObject json = UserRestTestUtil.usernameJson();
                final RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                final Response response = request.get(GET_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetUserNotFound(response.getBody().asString());
            }

            @Test
            void url_assert_empty_list() throws JsonProcessingException {
                logger.trace(LOG_GET_URL_EMPTY_ARRAY);
                userDaoTestUtil.createUser();

                final String path = GET_ENDPOINT + '/' + UserQueryParameters.USERNAME;
                final RequestSpecification request = UserRestTestUtil.getRequest();

                final Response response = request.get(path);
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetUserNotFound(response.getBody().asString());
            }
        }

        @Nested
        class bad_request {

            @Test
            void json() {
                logger.trace(LOG_GET_JSON);
                final JSONObject json = UserRestTestUtil.usernameBADJson();
                final RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                final Response response = request.get(GET_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void url() {
                logger.trace(LOG_GET_URL);
                final String path = GET_ENDPOINT + "/q";
                final RequestSpecification request = UserRestTestUtil.getRequest();

                final Response response = request.get(path);
                assertThat(response.getStatusCode(), equalTo(400));
            }
        }
    }

    @Nested
    class get_user_questions {

        @Nested
        class success {

            @Test
            void json() throws JsonProcessingException {
                logger.trace(LOG_GET_QUESTIONS_JSON);
                questionDaoTestUtil.createManyQuestions(UserDaoTestUtil.RESULT_SIZE);

                final long userId = 1L;
                final int page = 1;

                final JSONObject json = UserRestTestUtil.idPageJSON(userId, page);
                final RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                final Response response = request.get(GET_QUESTIONS_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetQuestions(response.getBody().asString());
            }

            @Test
            void url() throws JsonProcessingException {
                logger.trace(LOG_GET_QUESTIONS_URL);
                questionDaoTestUtil.createManyQuestions(UserDaoTestUtil.RESULT_SIZE);

                final String path = GET_QUESTIONS_ENDPOINT + "/1/1";
                final RequestSpecification request = UserRestTestUtil.getRequest();

                final Response response = request.get(path);
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetQuestions(response.getBody().asString());
            }
        }

        @Nested
        class bad_request {

            @Test
            void json() {
                logger.trace(LOG_GET_QUESTIONS_JSON);
                questionDaoTestUtil.createManyQuestions(UserDaoTestUtil.RESULT_SIZE);

                final long userId = -1L;
                final int page = 0;

                final JSONObject json = UserRestTestUtil.idPageJSON(userId, page);
                final RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                Response response = request.get(GET_QUESTIONS_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void url() {
                logger.trace(LOG_GET_QUESTIONS_URL);
                questionDaoTestUtil.createManyQuestions(UserDaoTestUtil.RESULT_SIZE);

                final String path = GET_QUESTIONS_ENDPOINT + "/-1/0";
                final RequestSpecification request = UserRestTestUtil.getRequest();

                final Response response = request.get(path);
                assertThat(response.getStatusCode(), equalTo(400));
            }
        }

        @Nested
        class not_found {

            @Nested
            class user_not_exist {

                @Test
                void json() {
                    logger.trace(LOG_GET_QUESTIONS_JSON);

                    final long userId = 1L;
                    final int page = 1;

                    final JSONObject json = UserRestTestUtil.idPageJSON(userId, page);
                    final RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                    final Response response = request.get(GET_QUESTIONS_ENDPOINT);
                    assertThat(response.getStatusCode(), equalTo(404));
                }

                @Test
                void url() {
                    logger.trace(LOG_GET_QUESTIONS_URL);
                    final String path = GET_QUESTIONS_ENDPOINT + "/1/1";
                    final RequestSpecification request = UserRestTestUtil.getRequest();

                    final Response response = request.get(path);
                    assertThat(response.getStatusCode(), equalTo(404));
                }
            }

            @Nested
            class question_assert_empty_list {

                @Test
                void json() throws JsonProcessingException {
                    logger.trace(LOG_GET_QUESTIONS_EMPTY_LIST);
                    userDaoTestUtil.createUser();

                    final long userId = 1L;
                    final int page = 1;

                    final JSONObject json = UserRestTestUtil.idPageJSON(userId, page);
                    final RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                    final Response response = request.get(GET_QUESTIONS_ENDPOINT);
                    assertThat(response.getStatusCode(), equalTo(200));

                    final ObjectMapper mapper = UserRestTestUtil.getObjectMapper();
                    final UserQuestionsResponse[] questionsResponse = mapper.readValue(response.body().asString(), UserQuestionsResponse[].class);
                    assertThat(questionsResponse.length, equalTo(0));
                }

                @Test
                void url() throws JsonProcessingException {
                    logger.trace(LOG_GET_QUESTIONS_EMPTY_LIST);
                    userDaoTestUtil.createUser();

                    final String path = GET_QUESTIONS_ENDPOINT + "/1/1";
                    final RequestSpecification request = UserRestTestUtil.getRequest();

                    final Response response = request.get(path);
                    assertThat(response.getStatusCode(), equalTo(200));

                    final ObjectMapper mapper = UserRestTestUtil.getObjectMapper();
                    final UserQuestionsResponse[] questionsResponse = mapper.readValue(response.body().asString(), UserQuestionsResponse[].class);
                    assertThat(questionsResponse.length, equalTo(0));
                }
            }
        }
    }

    @Nested
    class get_user_answers {

        @Nested
        class success {

            @Test
            void json() throws JsonProcessingException {
                logger.trace(LOG_GET_ANSWERS_JSON);
                answerDaoTestUtil.createManyAnswers(UserDaoTestUtil.RESULT_SIZE);

                final long userId = 1L;
                final int page = 1;

                final JSONObject json = UserRestTestUtil.idPageJSON(userId, page);
                final RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                final Response response = request.get(GET_ANSWERS_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetAnswers(response.getBody().asString());
            }

            @Test
            void url() throws JsonProcessingException {
                logger.trace(LOG_GET_ANSWERS_URL);
                answerDaoTestUtil.createManyAnswers(UserDaoTestUtil.RESULT_SIZE);

                final String path = GET_ANSWERS_ENDPOINT + "/1/1";
                final RequestSpecification request = UserRestTestUtil.getRequest();

                final Response response = request.get(path);
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetAnswers(response.getBody().asString());
            }
        }

        @Nested
        class bad_request {

            @Test
            void json() {
                logger.trace(LOG_GET_ANSWERS_JSON);
                answerDaoTestUtil.createManyAnswers(UserDaoTestUtil.RESULT_SIZE);

                final long userId = -1L;
                final int page = 0;

                final JSONObject json = UserRestTestUtil.idPageJSON(userId, page);
                final RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                Response response = request.get(GET_ANSWERS_ENDPOINT);
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void url() {
                logger.trace(LOG_GET_ANSWERS_URL);
                answerDaoTestUtil.createManyAnswers(UserDaoTestUtil.RESULT_SIZE);

                final String path = GET_ANSWERS_ENDPOINT + "/-1/0";
                final RequestSpecification request = RestAssured.given();

                final Response response = request.get(path);
                assertThat(response.getStatusCode(), equalTo(400));
            }
        }

        @Nested
        class not_found {

            @Nested
            class user_not_exist {

                @Test
                void json() {
                    logger.trace(LOG_GET_ANSWERS_JSON);

                    final long userId = 1L;
                    final int page = 1;

                    final JSONObject json = UserRestTestUtil.idPageJSON(userId, page);
                    final RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                    final Response response = request.get(GET_ANSWERS_ENDPOINT);
                    assertThat(response.getStatusCode(), equalTo(404));
                }

                @Test
                void url() {
                    logger.trace(LOG_GET_ANSWERS_URL);

                    final String path = GET_ANSWERS_ENDPOINT + "/1/1";
                    final RequestSpecification request = UserRestTestUtil.getRequest();

                    final Response response = request.get(path);
                    assertThat(response.getStatusCode(), equalTo(404));
                }
            }

            @Nested
            class answer_assert_empty_list {

                @Test
                void json() throws JsonProcessingException {
                    logger.trace(LOG_GET_ANSWERS_EMPTY_LIST);
                    userDaoTestUtil.createUser();

                    final long userId = 1L;
                    final int page = 1;

                    final JSONObject json = UserRestTestUtil.idPageJSON(userId, page);
                    final RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                    final Response response = request.get(GET_ANSWERS_ENDPOINT);
                    assertThat(response.getStatusCode(), equalTo(200));

                    final ObjectMapper objectMapper = UserRestTestUtil.getObjectMapper();
                    final UserAnswersResponse[] answersResponse = objectMapper.readValue(response.getBody().asString(), UserAnswersResponse[].class);
                    assertThat(answersResponse.length, equalTo(0));
                }

                @Test
                void url() throws JsonProcessingException {
                    logger.trace(LOG_GET_ANSWERS_EMPTY_LIST);
                    userDaoTestUtil.createUser();

                    final String path = GET_ANSWERS_ENDPOINT + "/1/1";
                    final RequestSpecification request = UserRestTestUtil.getRequest();

                    final Response response = request.get(path);
                    assertThat(response.getStatusCode(), equalTo(200));

                    final ObjectMapper objectMapper = UserRestTestUtil.getObjectMapper();
                    final UserAnswersResponse[] answersResponse = objectMapper.readValue(response.getBody().asString(), UserAnswersResponse[].class);
                    assertThat(answersResponse.length, equalTo(0));
                }
            }
        }
    }

    private void assertCorrectDataGetAnswers(String json) throws JsonProcessingException {
        final ObjectMapper objectMapper = UserRestTestUtil.getObjectMapper();
        final UserAnswersResponse[] answersResponse = objectMapper.readValue(json, UserAnswersResponse[].class);
        assertThat(answersResponse.length, greaterThan(0));

        for (UserAnswersResponse userAnswersResponse : answersResponse) {
            assertThat(userAnswersResponse, notNullValue());
            assertThat(userAnswersResponse.getAnswerId(), notNullValue());
            assertThat(userAnswersResponse.getText(), notNullValue());
        }
    }

    private void assertCorrectDataGetQuestions(String json) throws JsonProcessingException {
        final ObjectMapper objectMapper = UserRestTestUtil.getObjectMapper();
        final UserQuestionsResponse[] questionsResponse = objectMapper.readValue(json, UserQuestionsResponse[].class);
        assertThat(questionsResponse.length, greaterThan(0));

        for (UserQuestionsResponse userQuestionsResponse : questionsResponse) {
            assertThat(userQuestionsResponse, notNullValue());
            assertThat(userQuestionsResponse.getQuestionId(), notNullValue());
            assertThat(userQuestionsResponse.getTitle(), notNullValue());
        }
    }

    private void assertCorrectDataGetUserNotFound(String json) throws JsonProcessingException {
        final ObjectMapper mapper = UserRestTestUtil.getObjectMapper();
        final UserFullResponse userFullResponse = mapper.readValue(json, UserFullResponse.class);

        assertThat(userFullResponse, notNullValue());
        assertThat(userFullResponse.getUserId(), notNullValue());
        assertThat(userFullResponse.getAbout(), notNullValue());
        assertThat(userFullResponse.getUsername(), notNullValue());

        assertThat(userFullResponse.getQuestions(), notNullValue());
        assertThat(userFullResponse.getQuestions(), equalTo(Collections.emptyList()));

        assertThat(userFullResponse.getAnswers(), notNullValue());
        assertThat(userFullResponse.getAnswers(), equalTo(Collections.emptyList()));
    }

    private void assertCorrectDataGetUserSuccess(String json) throws JsonProcessingException {
        final ObjectMapper mapper = UserRestTestUtil.getObjectMapper();
        final UserFullResponse userFullResponse = mapper.readValue(json, UserFullResponse.class);

        assertThat(userFullResponse, notNullValue());
        assertThat(userFullResponse.getUserId(), notNullValue());
        assertThat(userFullResponse.getAbout(), notNullValue());
        assertThat(userFullResponse.getUsername(), notNullValue());

        assertThat(userFullResponse.getQuestions().size(), greaterThan(0));
        assertThat(userFullResponse.getAnswers().size(), greaterThan(0));

        for (Question q : userFullResponse.getQuestions()) {
            assertThat(q.getId(), notNullValue());
            assertThat(q.getTitle(), notNullValue());
        }

        for (Answer a : userFullResponse.getAnswers()) {
            assertThat(a.getId(), notNullValue());
            assertThat(a.getText(), notNullValue());
        }
    }
}
