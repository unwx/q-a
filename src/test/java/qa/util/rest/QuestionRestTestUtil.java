package qa.util.rest;

import org.json.JSONObject;
import qa.util.dao.query.params.QuestionQueryParameters;

import java.util.Arrays;

public class QuestionRestTestUtil extends RestTestUtil {

    private QuestionRestTestUtil() {
    }

    public static JSONObject createQuestionJson() {
        JSONObject json = new JSONObject();
        json.put("title", QuestionQueryParameters.TITLE);
        json.put("text", QuestionQueryParameters.TEXT);
        json.put("tags", Arrays.asList(QuestionQueryParameters.TAGS_ARRAY));
        return json;
    }

    public static JSONObject createBADQuestionJson() {
        JSONObject json = new JSONObject();
        json.put("title", "a");
        json.put("text", "b");
        json.put("tags", "123-./,");
        return json;
    }

    public static JSONObject editQuestionJson() {
        JSONObject json = new JSONObject();
        json.put("text", QuestionQueryParameters.SECOND_TEXT);
        json.put("tags", Arrays.asList(QuestionQueryParameters.TAGS_ARRAY));
        json.put("id", 1);
        return json;
    }

    public static JSONObject editBADQuestionJson() {
        JSONObject json = new JSONObject();
        json.put("text", "b");
        json.put("tags", "123-./,");
        json.put("id", -1L);
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

    public static JSONObject page() {
        JSONObject json = new JSONObject();
        json.put("page", 1);
        return json;
    }

    public static JSONObject badPage() {
        JSONObject json = new JSONObject();
        json.put("page", -1);
        return json;
    }
}
