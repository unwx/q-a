package qa.rest;

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
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class UserRestControllerTest {

    private final SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private static final String username = "user123";
    private static final String requiredResult =
            """
            {\
            "username":"%s",\
            "about":null,\
            "questions":[\
            {\
            "id":1,\
            "title":"title"\
            },\
            {\
            "id":2,\
            "title":"title"\
            },\
            {\
            "id":3,\
            "title":"title"\
            }\
            ],\
            "answers":[\
            {\
            "id":1,\
            "text":"text"\
            },\
            {\
            "id":2,\
            "text":"text"\
            }\
            ]\
            }\
            """.formatted(username);

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
    void getUser_Success_ByPathVariable() {
        createUserWithQuestionsAndAnswers();
        RequestSpecification request = RestAssured.given();
        Response response = request.get("get/" + username);
        assertThat(response.getStatusCode(), equalTo(200));
        assertThat(response.getBody().asString(), equalTo(requiredResult));
    }

    private void createUserWithQuestionsAndAnswers() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String userSql =
                    """
                    insert into usr (id, about, username) values (1, null, '%s')
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
}
