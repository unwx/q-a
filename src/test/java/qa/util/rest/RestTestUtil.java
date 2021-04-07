package qa.util.rest;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public abstract class RestTestUtil {

    protected RestTestUtil() {
    }

    public static RequestSpecification getRequestJson(String json) {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(json);
        return request;
    }

    public static RequestSpecification getRequestJsonJwt(String json, String token) {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        request.body(json);
        return request;
    }

    public static RequestSpecification getRequestJwt(String token) {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer_" + token);
        return request;
    }

    public static RequestSpecification getRequest() {
        return RestAssured.given();
    }
}
