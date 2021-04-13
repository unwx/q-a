package qa.util.rest;

import org.json.JSONObject;
import qa.util.dao.query.params.CommentQueryParameters;

public class CommentRestTestUtil extends RestTestUtil {

    private CommentRestTestUtil() {
    }

    public static JSONObject commentEditJson() {
        JSONObject json = new JSONObject();
        json.put("id", 1L);
        json.put("text", CommentQueryParameters.SECOND_TEXT);
        return json;
    }

    public static JSONObject commentAnswerCreateJson() {
        JSONObject json = new JSONObject();
        json.put("answer_id", 1L);
        json.put("text", CommentQueryParameters.TEXT);
        return json;
    }

    public static JSONObject commentQuestionCreateJson() {
        JSONObject json = new JSONObject();
        json.put("question_id", 1L);
        json.put("text", CommentQueryParameters.TEXT);
        return json;
    }

    public static JSONObject id() {
        JSONObject json = new JSONObject();
        json.put("id", 1L);
        return json;
    }

    public static JSONObject commentBADEditJson() {
        JSONObject json = new JSONObject();
        json.put("id", -1L);
        json.put("text", "wut");
        return json;
    }


    public static JSONObject commentAnswerBADCreateJson() {
        JSONObject json = new JSONObject();
        json.put("answer_id", -1L);
        json.put("text", "wut");
        return json;
    }

    public static JSONObject commentQuestionBADCreateJson() {
        JSONObject json = new JSONObject();
        json.put("question_id", -1L);
        json.put("text", "wut");
        return json;
    }

    public static JSONObject badId() {
        JSONObject json = new JSONObject();
        json.put("id", -1L);
        return json;
    }

}
