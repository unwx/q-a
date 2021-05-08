package util.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import java.text.SimpleDateFormat;

public abstract class RestTestUtil {

    protected RestTestUtil() {}

    public static ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String format = "yyyy-MM-dd HH:mm:ss";

        objectMapper.setDateFormat(new SimpleDateFormat(format));
        return objectMapper;
    }

    public static RequestSpecification getRequestJson(String json) {
        final RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(json);
        return request;
    }

    public static RequestSpecification getRequestJsonJwt(String json, String token) {
        final RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", token);
        request.body(json);
        return request;
    }

    public static RequestSpecification getRequestJwt(String token) {
        final RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization", token);
        return request;
    }

    public static RequestSpecification getRequest() {
        return RestAssured.given();
    }
}
