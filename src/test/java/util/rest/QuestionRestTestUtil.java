package util.rest;

import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import util.dao.query.params.QuestionQueryParameters;

import java.util.Arrays;

public class QuestionRestTestUtil extends RestTestUtil {

    private static final String ID        = "id";
    private static final String PAGE      = "page";
    private static final String TITLE     = "title";
    private static final String TEXT      = "text";
    private static final String TAGS      = "tags";

    private QuestionRestTestUtil() {}

    public static RequestSpecification createQuestionRequest(String accessToken) {
        final JSONObject json = createQuestionJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification createQuestionBadRequest(String accessToken) {
        final JSONObject json = badCreateQuestionJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editQuestionRequest(String accessToken) {
        final JSONObject json = editQuestionJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editQuestionRequest(long questionId, String accessToken) {
        final JSONObject json = editQuestionJson(questionId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editQuestionBadRequest(String accessToken) {
        final JSONObject json = badEditQuestionJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification idRequest(String accessToken) {
        final JSONObject json = id();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification idRequest() {
        final JSONObject json = id();
        return getRequestJson(json.toString());
    }

    public static RequestSpecification idRequest(long questionId, String accessToken) {
        final JSONObject json = id(questionId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification idBadRequest(String accessToken) {
        final JSONObject json = badId();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification idBadRequest() {
        final JSONObject json = badId();
        return getRequestJson(json.toString());
    }

    public static RequestSpecification pageRequest() {
        final JSONObject json = page();
        return getRequestJson(json.toString());
    }

    public static RequestSpecification pageBadRequest() {
        final JSONObject json = badPage();
        return getRequestJson(json.toString());
    }

    private static JSONObject createQuestionJson() {
        final JSONObject json = new JSONObject();
        json.put(TITLE, QuestionQueryParameters.TITLE);
        json.put(TEXT, QuestionQueryParameters.TEXT);
        json.put(TAGS, Arrays.asList(QuestionQueryParameters.TAGS_ARRAY));
        return json;
    }

    private static JSONObject badCreateQuestionJson() {
        final JSONObject json = new JSONObject();
        json.put(TITLE, "a");
        json.put(TEXT, "b");
        json.put(TAGS, "123-./,");
        return json;
    }

    private static JSONObject editQuestionJson() {
        final JSONObject json = new JSONObject();
        json.put(TEXT, QuestionQueryParameters.SECOND_TEXT);
        json.put(TAGS, Arrays.asList(QuestionQueryParameters.TAGS_ARRAY));
        json.put(ID, 1);
        return json;
    }

    private static JSONObject editQuestionJson(long questionId) {
        final JSONObject json = new JSONObject();
        json.put(TEXT, QuestionQueryParameters.SECOND_TEXT);
        json.put(TAGS, Arrays.asList(QuestionQueryParameters.TAGS_ARRAY));
        json.put(ID, questionId);
        return json;
    }

    private static JSONObject badEditQuestionJson() {
        final JSONObject json = new JSONObject();
        json.put(TEXT, "b");
        json.put(TAGS, "123-./,");
        json.put(ID, -1L);
        return json;
    }

    private static JSONObject id() {
        final JSONObject json = new JSONObject();
        json.put(ID, 1L);
        return json;
    }

    private static JSONObject id(long questionId) {
        final JSONObject json = new JSONObject();
        json.put(ID, questionId);
        return json;
    }

    private static JSONObject badId() {
        final JSONObject json = new JSONObject();
        json.put(ID, -1L);
        return json;
    }

    private static JSONObject page() {
        final JSONObject json = new JSONObject();
        json.put(PAGE, 1);
        return json;
    }

    private static JSONObject badPage() {
        final JSONObject json = new JSONObject();
        json.put(PAGE, -1);
        return json;
    }
}
