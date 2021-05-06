package util.rest;

import org.json.JSONObject;
import util.dao.query.params.AnswerQueryParameters;

public class AnswerRestTestUtil extends RestTestUtil {

    private AnswerRestTestUtil() {
    }

    public static JSONObject createAnswerJson() {
        JSONObject json = new JSONObject();
        json.put("question_id", 1L);
        json.put("text", AnswerQueryParameters.TEXT);
        return json;
    }

    public static JSONObject createAnswerJson(long questionId) {
        JSONObject json = new JSONObject();
        json.put("question_id", questionId);
        json.put("text", AnswerQueryParameters.TEXT);
        return json;
    }

    public static JSONObject createBADAnswerJson() {
        JSONObject json = new JSONObject();
        json.put("question_id", -1L);
        json.put("text", ".,m--x.m");
        return json;
    }

    public static JSONObject editAnswerJson() {
        JSONObject json = new JSONObject();
        json.put("id", 1L);
        json.put("text", AnswerQueryParameters.SECOND_TEXT);
        return json;
    }

    public static JSONObject editAnswerJson(long answerId) {
        JSONObject json = new JSONObject();
        json.put("id", answerId);
        json.put("text", AnswerQueryParameters.SECOND_TEXT);
        return json;
    }

    public static JSONObject editBADAnswerJson() {
        JSONObject json = new JSONObject();
        json.put("id", -1L);
        json.put("text", ".,m--x.m");
        return json;
    }

    public static JSONObject id() {
        JSONObject json = new JSONObject();
        json.put("id", 1L);
        return json;
    }

    public static JSONObject id(long id) {
        JSONObject json = new JSONObject();
        json.put("id", id);
        return json;
    }

    public static JSONObject badId() {
        JSONObject json = new JSONObject();
        json.put("id", -1L);
        return json;
    }

    public static JSONObject getAnswerJson() {
        JSONObject json = new JSONObject();
        json.put("id", 1L);
        json.put("page", 1);
        return json;
    }

    public static JSONObject badGetAnswerJson() {
        JSONObject json = new JSONObject();
        json.put("id", 1L);
        json.put("page", 0);
        return json;
    }
}
