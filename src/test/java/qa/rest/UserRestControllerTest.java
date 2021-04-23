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
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.domain.Answer;
import qa.domain.Question;
import qa.dto.response.user.UserAnswersResponse;
import qa.dto.response.user.UserFullResponse;
import qa.dto.response.user.UserQuestionsResponse;
import qa.logger.TestLogger;
import qa.tools.annotations.SpringIntegrationTest;
import qa.util.dao.AnswerDaoTestUtil;
import qa.util.dao.QuestionDaoTestUtil;
import qa.util.dao.UserDaoTestUtil;
import qa.util.dao.query.params.UserQueryParameters;
import qa.util.hibernate.HibernateSessionFactoryUtil;
import qa.util.mock.JedisMockTestUtil;
import qa.util.rest.UserRestTestUtil;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringIntegrationTest
public class UserRestControllerTest {

    private SessionFactory sessionFactory;
    private UserDaoTestUtil userDaoTestUtil;
    private AnswerDaoTestUtil answerDaoTestUtil;
    private QuestionDaoTestUtil questionDaoTestUtil;

    @Autowired
    private JedisResourceCenter jedisResourceCenter;

    private final TestLogger logger = new TestLogger(UserRestControllerTest.class);

    @BeforeAll
    void init() {
        sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
        userDaoTestUtil = new UserDaoTestUtil(sessionFactory);
        answerDaoTestUtil = new AnswerDaoTestUtil(sessionFactory, jedisResourceCenter);
        questionDaoTestUtil = new QuestionDaoTestUtil(sessionFactory, jedisResourceCenter);
    }

    @BeforeEach
    void truncate() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table question cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
            transaction.commit();
            RestAssured.baseURI = "http://localhost:8080/api/v1/user/";
            RestAssured.port = 8080;
        }
        JedisResource resource = jedisResourceCenter.getResource();
        resource.getJedis().flushDB();
        resource.close();
    }


    @Nested
    class get_user {

        @Nested
        class success {

            @Test
            void url() throws JsonProcessingException {
                logger.trace("by url");
                questionDaoTestUtil.createManyQuestionsWithManyAnswers(UserDaoTestUtil.RESULT_SIZE, 2);
                RequestSpecification request = UserRestTestUtil.getRequest();

                Response response = request.get("get/" + UserQueryParameters.USERNAME);
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetUserSuccess(response.getBody().asString());
            }

            @Test
            void json() throws JsonProcessingException {
                logger.trace("by json");
                questionDaoTestUtil.createManyQuestionsWithManyAnswers(UserDaoTestUtil.RESULT_SIZE, 2);
                JSONObject json = UserRestTestUtil.usernameJson();
                RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                Response response = request.get("get");
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetUserSuccess(response.getBody().asString());
            }
        }

        @Nested
        class not_found {

            @Test
            void json_assert_empty_list() throws JsonProcessingException {
                logger.trace("by json. assert response is empty array");
                userDaoTestUtil.createUser();
                JSONObject json = UserRestTestUtil.usernameJson();
                RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                Response response = request.get("get");
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetUserNotFound(response.getBody().asString());
            }

            @Test
            void url_assert_empty_list() throws JsonProcessingException {
                logger.trace("by url. assert response is empty array");
                userDaoTestUtil.createUser();
                RequestSpecification request = UserRestTestUtil.getRequest();

                Response response = request.get("get/" + UserQueryParameters.USERNAME);
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetUserNotFound(response.getBody().asString());
            }
        }

        @Nested
        class bad_request {

            @Test
            void json() {
                logger.trace("by json");
                JSONObject json = UserRestTestUtil.usernameBADJson();
                RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                Response response = request.get("questions/get");
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void url() {
                logger.trace("by url");
                RequestSpecification request = UserRestTestUtil.getRequest();

                Response response = request.get("get/" + "o");
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
                logger.trace("by json");
                questionDaoTestUtil.createManyQuestions(UserDaoTestUtil.RESULT_SIZE);
                JSONObject json = UserRestTestUtil.idPageJSON(1, 1);
                RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                Response response = request.get("questions/get");
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetQuestions(response.getBody().asString());
            }

            @Test
            void url() throws JsonProcessingException {
                logger.trace("by url");
                questionDaoTestUtil.createManyQuestions(UserDaoTestUtil.RESULT_SIZE);
                RequestSpecification request = UserRestTestUtil.getRequest();

                Response response = request.get("questions/get/1/1");
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetQuestions(response.getBody().asString());
            }
        }

        @Nested
        class bad_request {

            @Test
            void json() {
                logger.trace("by json");
                questionDaoTestUtil.createManyQuestions(UserDaoTestUtil.RESULT_SIZE);
                JSONObject json = UserRestTestUtil.idPageJSON(1, 0);
                RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                Response response = request.get("questions/get");
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void url() {
                logger.trace("by url");
                questionDaoTestUtil.createManyQuestions(UserDaoTestUtil.RESULT_SIZE);
                RequestSpecification request = UserRestTestUtil.getRequest();

                Response response = request.get("questions/get/1/0");
                assertThat(response.getStatusCode(), equalTo(400));
            }
        }

        @Nested
        class not_found {

            @Nested
            class user_not_exist {

                @Test
                void json() {
                    logger.trace("by json");
                    JSONObject json = UserRestTestUtil.idPageJSON(1, 1);
                    RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                    Response response = request.get("questions/get");
                    assertThat(response.getStatusCode(), equalTo(404));
                }

                @Test
                void url() {
                    logger.trace("by url");
                    RequestSpecification request = UserRestTestUtil.getRequest();

                    Response response = request.get("questions/get/1/1");
                    assertThat(response.getStatusCode(), equalTo(404));
                }
            }

            @Nested
            class question_assert_empty_list {

                @Test
                void json() throws JsonProcessingException {
                    logger.trace("by json");
                    userDaoTestUtil.createUser();
                    JSONObject json = UserRestTestUtil.idPageJSON(1, 1);
                    RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                    Response response = request.get("questions/get");
                    assertThat(response.getStatusCode(), equalTo(200));

                    ObjectMapper mapper = new ObjectMapper();
                    UserQuestionsResponse[] questionsResponse = mapper.readValue(response.body().asString(), UserQuestionsResponse[].class);
                    assertThat(questionsResponse.length, equalTo(0));
                }

                @Test
                void url() throws JsonProcessingException {
                    logger.trace("by url");
                    userDaoTestUtil.createUser();
                    RequestSpecification request = UserRestTestUtil.getRequest();

                    Response response = request.get("questions/get/1/1");
                    assertThat(response.getStatusCode(), equalTo(200));

                    ObjectMapper mapper = new ObjectMapper();
                    UserQuestionsResponse[] questionsResponse = mapper.readValue(response.body().asString(), UserQuestionsResponse[].class);
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
                logger.trace("by json");
                answerDaoTestUtil.createManyAnswers(UserDaoTestUtil.RESULT_SIZE);
                JSONObject json = UserRestTestUtil.idPageJSON(1, 1);
                RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                Response response = request.get("answers/get");
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetAnswers(response.getBody().asString());
            }

            @Test
            void url() throws JsonProcessingException {
                logger.trace("by url");
                answerDaoTestUtil.createManyAnswers(UserDaoTestUtil.RESULT_SIZE);
                RequestSpecification request = UserRestTestUtil.getRequest();

                Response response = request.get("answers/get/1/1");
                assertThat(response.getStatusCode(), equalTo(200));

                assertCorrectDataGetAnswers(response.getBody().asString());
            }
        }

        @Nested
        class bad_request {

            @Test
            void json() {
                logger.trace("by json");
                answerDaoTestUtil.createManyAnswers(UserDaoTestUtil.RESULT_SIZE);
                JSONObject json = UserRestTestUtil.idPageJSON(1, 0);
                RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                Response response = request.get("answers/get");
                assertThat(response.getStatusCode(), equalTo(400));
            }

            @Test
            void url() {
                logger.trace("by url");
                answerDaoTestUtil.createManyAnswers(UserDaoTestUtil.RESULT_SIZE);
                RequestSpecification request = RestAssured.given();

                Response response = request.get("answers/get/1/0");
                assertThat(response.getStatusCode(), equalTo(400));
            }
        }

        @Nested
        class not_found {

            @Nested
            class user_not_exist {

                @Test
                void json() {
                    logger.trace("by json");
                    JSONObject json = UserRestTestUtil.idPageJSON(1, 1);
                    RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                    Response response = request.get("answers/get");
                    assertThat(response.getStatusCode(), equalTo(404));
                }

                @Test
                void url() {
                    logger.trace("by url");
                    RequestSpecification request = UserRestTestUtil.getRequest();

                    Response response = request.get("answers/get/1/1");
                    assertThat(response.getStatusCode(), equalTo(404));
                }
            }

            @Nested
            class answer_assert_empty_list {

                @Test
                void json() throws JsonProcessingException {
                    logger.trace("by json");
                    userDaoTestUtil.createUser();
                    JSONObject json = UserRestTestUtil.idPageJSON(1, 1);
                    RequestSpecification request = UserRestTestUtil.getRequestJson(json.toString());

                    Response response = request.get("answers/get");
                    assertThat(response.getStatusCode(), equalTo(200));

                    ObjectMapper objectMapper = new ObjectMapper();
                    UserAnswersResponse[] answersResponse = objectMapper.readValue(response.getBody().asString(), UserAnswersResponse[].class);
                    assertThat(answersResponse.length, equalTo(0));
                }

                @Test
                void url() throws JsonProcessingException {
                    logger.trace("by url");
                    userDaoTestUtil.createUser();
                    RequestSpecification request = UserRestTestUtil.getRequest();

                    Response response = request.get("answers/get/1/1");
                    assertThat(response.getStatusCode(), equalTo(200));

                    ObjectMapper objectMapper = new ObjectMapper();
                    UserAnswersResponse[] answersResponse = objectMapper.readValue(response.getBody().asString(), UserAnswersResponse[].class);
                    assertThat(answersResponse.length, equalTo(0));
                }
            }
        }
    }

    private void assertCorrectDataGetAnswers(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserAnswersResponse[] answersResponse = objectMapper.readValue(json, UserAnswersResponse[].class);
        for (UserAnswersResponse userAnswersResponse : answersResponse) {
            assertThat(userAnswersResponse, notNullValue());
            assertThat(userAnswersResponse.getAnswerId(), notNullValue());
            assertThat(userAnswersResponse.getText(), notNullValue());
        }
    }

    private void assertCorrectDataGetQuestions(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        UserQuestionsResponse[] questionsResponse = mapper.readValue(json, UserQuestionsResponse[].class);
        assertThat(questionsResponse.length, greaterThan(0));

        for (UserQuestionsResponse userQuestionsResponse : questionsResponse) {
            assertThat(userQuestionsResponse, notNullValue());
            assertThat(userQuestionsResponse.getQuestionId(), notNullValue());
            assertThat(userQuestionsResponse.getTitle(), notNullValue());
        }
    }

    private void assertCorrectDataGetUserNotFound(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        UserFullResponse userFullResponse = mapper.readValue(json, UserFullResponse.class);

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
        ObjectMapper mapper = new ObjectMapper();
        UserFullResponse userFullResponse = mapper.readValue(json, UserFullResponse.class);

        assertThat(userFullResponse, notNullValue());
        assertThat(userFullResponse.getUserId(), notNullValue());
        assertThat(userFullResponse.getAbout(), notNullValue());
        assertThat(userFullResponse.getUsername(), notNullValue());

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
