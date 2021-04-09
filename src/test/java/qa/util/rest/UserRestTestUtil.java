package qa.util.rest;

import org.json.JSONObject;
import qa.util.dao.query.params.UserQueryParameters;

public class UserRestTestUtil extends RestTestUtil {
    private UserRestTestUtil() {
    }

    public static JSONObject usernameJson() {
        JSONObject json = new JSONObject();
        json.put("username", UserQueryParameters.USERNAME);
        return json;
    }

    public static JSONObject usernameBADJson() {
        JSONObject json = new JSONObject();
        json.put("username", UserQueryParameters.USERNAME);
        return json;
    }

    public static JSONObject idPageJSON(int id, int page) {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("page", page);
        return json;
    }
}
