package util.rest;

import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import util.dao.query.params.AnswerQueryParameters;

public class AnswerRestTestUtil extends RestTestUtil {

    private static final String ID              = "id";
    private static final String TEXT            = "text";
    private static final String PAGE            = "page";
    private static final String QUESTION_ID     = "question_id";

    private AnswerRestTestUtil() {}

    public static RequestSpecification createAnswerRequest(String accessToken) {
        final JSONObject json = createAnswerJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification createAnswerRequest(long questionId, String accessToken) {
        final JSONObject json = createAnswerJson(questionId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification createAnswerBadRequest(String accessToken) {
        final JSONObject json = badCreateAnswerJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editAnswerRequest(String accessToken) {
        final JSONObject json = editAnswerJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editAnswerRequest(long answerId, String accessToken) {
        final JSONObject json = editAnswerJson(answerId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editAnswerBadRequest(String accessToken) {
        final JSONObject json = badEditAnswerJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification getAnswerRequest() {
        final JSONObject json = getAnswerJson();
        return getRequestJson(json.toString());
    }

    public static RequestSpecification getAnswerBadRequest() {
        final JSONObject json = badGetAnswerJson();
        return getRequestJson(json.toString());
    }

    public static RequestSpecification idRequest(String accessToken) {
        final JSONObject json = id();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification idRequest(long answerId, String accessToken) {
        final JSONObject json = id(answerId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification idBadRequest(String accessToken) {
        final JSONObject json = badId();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    private static JSONObject createAnswerJson() {
        final JSONObject json = new JSONObject();
        json.put(QUESTION_ID, 1L);
        json.put(TEXT, AnswerQueryParameters.TEXT);
        return json;
    }

    private static JSONObject createAnswerJson(long questionId) {
        final JSONObject json = new JSONObject();
        json.put(QUESTION_ID, questionId);
        json.put(TEXT, AnswerQueryParameters.TEXT);
        return json;
    }

    private static JSONObject badCreateAnswerJson() {
        final JSONObject json = new JSONObject();
        json.put(QUESTION_ID, -1L);
        json.put(TEXT, ".,m--x.m");
        return json;
    }

    private static JSONObject editAnswerJson() {
        final JSONObject json = new JSONObject();
        json.put(ID, 1L);
        json.put(TEXT, AnswerQueryParameters.SECOND_TEXT);
        return json;
    }

    private static JSONObject editAnswerJson(long answerId) {
        final JSONObject json = new JSONObject();
        json.put(ID, answerId);
        json.put(TEXT, AnswerQueryParameters.SECOND_TEXT);
        return json;
    }

    private static JSONObject badEditAnswerJson() {
        final JSONObject json = new JSONObject();
        json.put(ID, -1L);
        json.put(TEXT, ".,m--x.m");
        return json;
    }

    private static JSONObject id() {
        final JSONObject json = new JSONObject();
        json.put(ID, 1L);
        return json;
    }

    private static JSONObject id(long id) {
        final JSONObject json = new JSONObject();
        json.put(ID, id);
        return json;
    }

    private static JSONObject badId() {
        final JSONObject json = new JSONObject();
        json.put(ID, -1L);
        return json;
    }

    private static JSONObject getAnswerJson() {
        final JSONObject json = new JSONObject();
        json.put(ID, 1L);
        json.put(PAGE, 1);
        return json;
    }

    private static JSONObject badGetAnswerJson() {
        final JSONObject json = new JSONObject();
        json.put(ID, 1L);
        json.put(PAGE, 0);
        return json;
    }
}
