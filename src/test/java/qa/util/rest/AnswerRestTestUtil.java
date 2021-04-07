package qa.util.rest;

import org.json.JSONObject;
import qa.util.dao.query.params.AnswerQueryParameters;

public class AnswerRestTestUtil extends RestTestUtil {

    public static JSONObject createAnswerJson() {
        JSONObject json = new JSONObject();
        json.put("question_id", 1L);
        json.put("text", AnswerQueryParameters.TEXT);
        return json;
    }

    public static JSONObject editAnswerJson() {
        JSONObject json = new JSONObject();
        json.put("id", 1L);
        json.put("text", AnswerQueryParameters.SECOND_TEXT);
        return json;
    }
}
