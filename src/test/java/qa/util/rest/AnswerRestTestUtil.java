package qa.util.rest;

import org.json.JSONObject;
import qa.util.dao.query.params.AnswerQueryParameters;

public class AnswerRestTestUtil extends RestTestUtil {

    private AnswerRestTestUtil() {
    }

    public static JSONObject createAnswerJson() {
        JSONObject json = new JSONObject();
        json.put("question_id", 1L);
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

    public static JSONObject badId() {
        JSONObject json = new JSONObject();
        json.put("id", -1L);
        return json;
    }
}
