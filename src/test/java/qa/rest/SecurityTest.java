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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import qa.config.spring.SpringConfig;
import qa.dao.util.HibernateSessionFactoryConfigurer;
import qa.security.jwt.entity.JwtData;
import qa.security.jwt.service.JwtProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class SecurityTest {

    private final SessionFactory sessionFactory = HibernateSessionFactoryConfigurer.getSessionFactory();
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

    /* question controller example (user role required) */

    @Test
    void tokenSecurityFilter_WithoutToken() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body("{\"test\":123}");
        Response response = request.post("create");
        assertThat(response.getStatusCode(), equalTo(401));

        Response response1 = request.put("edit");
        assertThat(response1.getStatusCode(), equalTo(401));

        Response response2 = request.delete("delete");
        assertThat(response2.getStatusCode(), equalTo(401));
    }

    @Test
    void tokenSecurityFilter_InvalidToken() {
        JwtData data = jwtProvider.createAccess(defaultUserEmail);
        String token = data.getToken();

        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer_" + token);

        Response response = request.post("create");
        assertThat(response.getStatusCode(), equalTo(401));
    }
}
