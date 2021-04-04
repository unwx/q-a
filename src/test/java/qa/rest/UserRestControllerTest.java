package qa.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import qa.config.spring.SpringConfig;
import qa.domain.Answer;
import qa.domain.Question;
import qa.dto.response.user.UserAnswersResponse;
import qa.dto.response.user.UserFullResponse;
import qa.dto.response.user.UserQuestionsResponse;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.Collections;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class UserRestControllerTest {

    private final SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private static final String username = "user123";

    @BeforeEach
    void truncate() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table question cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
            transaction.commit();
            RestAssured.baseURI = "http://localhost:8080/api/v1/user/";
            RestAssured.port = 8080;
        }
    }

    @Test
    void getUser_Success_ByPathVariable() throws JsonProcessingException {
        createUserWithQuestionsAndAnswers();
        RequestSpecification request = RestAssured.given();
        Response response = request.get("get/" + username);
        assertThat(response.getStatusCode(), equalTo(200));
        ObjectMapper mapper = new ObjectMapper();
        UserFullResponse userFullResponse = mapper.readValue(response.getBody().asString(), UserFullResponse.class);

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

    @Test
    void getUser_Success_ByJson() throws JsonProcessingException {
        createUserWithQuestionsAndAnswers();
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body("{\"username\":\"" + username + "\"}");

        Response response = request.get("get");
        assertThat(response.getStatusCode(), equalTo(200));
        ObjectMapper mapper = new ObjectMapper();
        UserFullResponse userFullResponse = mapper.readValue(response.getBody().asString(), UserFullResponse.class);

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

    @Test
    void getUser_AssertCollectionsEmptyIfNotFound() throws JsonProcessingException {
        createUser();
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body("{\"username\":\"" + username + "\"}");

        Response response = request.get("get");
        assertThat(response.getStatusCode(), equalTo(200));
        ObjectMapper mapper = new ObjectMapper();
        UserFullResponse userFullResponse = mapper.readValue(response.getBody().asString(), UserFullResponse.class);

        assertThat(userFullResponse, notNullValue());
        assertThat(userFullResponse.getUserId(), notNullValue());
        assertThat(userFullResponse.getAbout(), notNullValue());
        assertThat(userFullResponse.getUsername(), notNullValue());

        assertThat(userFullResponse.getQuestions(), notNullValue());
        assertThat(userFullResponse.getQuestions(), equalTo(Collections.emptyList()));

        assertThat(userFullResponse.getAnswers(), notNullValue());
        assertThat(userFullResponse.getAnswers(), equalTo(Collections.emptyList()));
    }

    @Test
    void getUserQuestions_Success_ByJson() throws JsonProcessingException {
        createUserWithManyQuestions();
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body("{\"id\":1, \"page\":1}");

        Response response = request.get("questions/get");
        assertThat(response.getStatusCode(), equalTo(200));
        ObjectMapper mapper = new ObjectMapper();
        UserQuestionsResponse[] questionsResponse = mapper.readValue(response.body().asString(), UserQuestionsResponse[].class);
        assertThat(questionsResponse.length, notNullValue());

        for (UserQuestionsResponse userQuestionsResponse : questionsResponse) {
            assertThat(userQuestionsResponse, notNullValue());
            assertThat(userQuestionsResponse, notNullValue());
        }
    }

    @Test
    void getUserQuestions_Success_ByPathVariable() throws JsonProcessingException {
        createUserWithManyQuestions();
        RequestSpecification request = RestAssured.given();

        Response response = request.get("questions/get/1/1");
        assertThat(response.getStatusCode(), equalTo(200));
        ObjectMapper mapper = new ObjectMapper();
        UserQuestionsResponse[] questionsResponse = mapper.readValue(response.body().asString(), UserQuestionsResponse[].class);
        assertThat(questionsResponse.length, notNullValue());

        for (UserQuestionsResponse userQuestionsResponse : questionsResponse) {
            assertThat(userQuestionsResponse.getQuestionId(), notNullValue());
            assertThat(userQuestionsResponse.getTitle(), notNullValue());
        }
    }

    @Test
    void getUserQuestions_Failure_ByJson() {
        createUserWithManyQuestions();
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body("{\"id\":1, \"page\":0}");

        Response response = request.get("questions/get");
        assertThat(response.getStatusCode(), equalTo(400));
    }

    @Test
    void getUserQuestions_Failure_ByPathVariable() {
        createUserWithManyQuestions();
        RequestSpecification request = RestAssured.given();

        Response response = request.get("questions/get/1/0");
        assertThat(response.getStatusCode(), equalTo(400));
    }

    @Test
    void getUserQuestions_NotFound_ByJson() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body("{\"id\":1, \"page\":5}");

        Response response = request.get("questions/get");
        assertThat(response.getStatusCode(), equalTo(404));

        request.body("{\"id\":234, \"page\":1}");

        Response response1 = request.get("questions/get");
        assertThat(response1.getStatusCode(), equalTo(404));
    }

    @Test
    void getUserQuestions_NotFound_PathVariable() {
        createUserWithManyQuestions();
        RequestSpecification request = RestAssured.given();

        Response response = request.get("questions/get/1/5123");
        assertThat(response.getStatusCode(), equalTo(404));

        Response response1 = request.get("questions/get/234/1");
        assertThat(response1.getStatusCode(), equalTo(404));
    }

    @Test
    void getUserAnswersSuccess_ByJson() throws JsonProcessingException {
        createUserWithManyAnswers();
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body("{\"id\":1, \"page\":1}");

        Response response = request.get("answers/get");
        assertThat(response.getStatusCode(), equalTo(200));

        ObjectMapper objectMapper = new ObjectMapper();
        UserAnswersResponse[] answersResponse = objectMapper.readValue(response.getBody().asString(), UserAnswersResponse[].class);
        for (UserAnswersResponse userAnswersResponse : answersResponse) {
            assertThat(userAnswersResponse.getAnswerId(), notNullValue());
            assertThat(userAnswersResponse.getText(), notNullValue());
        }
    }

    @Test
    void getUserAnswersSuccess_PathVariable() throws JsonProcessingException {
        createUserWithManyAnswers();
        RequestSpecification request = RestAssured.given();

        Response response = request.get("answers/get/1/1");
        assertThat(response.getStatusCode(), equalTo(200));

        ObjectMapper objectMapper = new ObjectMapper();
        UserAnswersResponse[] answersResponse = objectMapper.readValue(response.getBody().asString(), UserAnswersResponse[].class);
        for (UserAnswersResponse userAnswersResponse : answersResponse) {
            assertThat(userAnswersResponse.getAnswerId(), notNullValue());
            assertThat(userAnswersResponse.getText(), notNullValue());
        }
    }

    @Test
    void getUserAnswers_Failure_ByJson() {
        createUserWithManyAnswers();
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body("{\"id\":1, \"page\":0}");

        Response response = request.get("answers/get");
        assertThat(response.getStatusCode(), equalTo(400));
    }

    @Test
    void getUserAnswers_Failure_PathVariable() {
        createUserWithManyAnswers();
        RequestSpecification request = RestAssured.given();

        Response response = request.get("answers/get/1/0");
        assertThat(response.getStatusCode(), equalTo(400));
    }

    @Test
    void getUserAnswers_NotFound_ByJson() {
        createUserWithManyAnswers();
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body("{\"id\":1, \"page\":5123}");

        Response response = request.get("answers/get");
        assertThat(response.getStatusCode(), equalTo(404));

        request.body("{\"id\":123, \"page\":1}");
        Response response1 = request.get("answers/get");
        assertThat(response1.getStatusCode(), equalTo(404));
    }

    @Test
    void getUserAnswers_NotFound_PathVariable() {
        createUserWithManyAnswers();
        RequestSpecification request = RestAssured.given();

        Response response = request.get("answers/get/1/123");
        assertThat(response.getStatusCode(), equalTo(404));

        Response response1 = request.get("answers/get/123/1");
        assertThat(response1.getStatusCode(), equalTo(404));
    }

    private void createUserWithQuestionsAndAnswers() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, 'about', '%s')
                    """.formatted(username);
            String questionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (:id, '%s', '%s', 'tag, tag', 'text', 'title', 1)
                    """.formatted(new Date(), new Date());
            String answerSql =
                    """
                    insert into answer (id, answered, creation_date, text, author_id, question_id)\s\
                    values (:id, false, '%s', 'text', 1, :id)
                    """.formatted(new Date());
            session.createSQLQuery(userSql).executeUpdate();

            for (long i = 1; i < 4; i++) {
                session.createSQLQuery(questionSql).setParameter("id", i).executeUpdate();
            }
            for (long i = 1; i < 3; i++) {
                session.createSQLQuery(answerSql).setParameter("id", i).executeUpdate();
            }
            transaction.commit();
        }
    }

    private void createUserWithManyQuestions() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, 'about', '%s')
                    """.formatted(username);
            String questionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (:id, '%s', '%s', 'tag, tag', 'text', :title, 1)
                    """.formatted(new Date(), new Date());

            session.createSQLQuery(userSql).executeUpdate();
            for (long i = 0; i < 50; i++) {
                session.createSQLQuery(questionSql)
                        .setParameter("id", i)
                        .setParameter("title", String.valueOf(i))
                        .executeUpdate();
            }
            transaction.commit();
        }
    }

    private void createUserWithManyAnswers() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, 'about', '%s')
                    """.formatted(username);
            String questionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (1, '%s', '%s', 'tag, tag', 'text', 'title', 1)
                    """.formatted(new Date(), new Date());
            String answerSql =
                    """
                    insert into answer (id, answered, creation_date, text, author_id, question_id)\s\
                    values (:id, false, '%s', :text, 1, 1)
                    """.formatted(new Date());

            session.createSQLQuery(userSql).executeUpdate();
            session.createSQLQuery(questionSql).executeUpdate();
            for (long i = 0; i < 50; i++) {
                session.createSQLQuery(answerSql)
                        .setParameter("id", i)
                        .setParameter("text", String.valueOf(i))
                        .executeUpdate();
            }
            transaction.commit();
        }
    }

    private void createUser() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, 'about', '%s')
                    """.formatted(username);
            session.createSQLQuery(userSql).executeUpdate();
            transaction.commit();
        }
    }
}
