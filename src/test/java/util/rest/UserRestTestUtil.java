package util.rest;

import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import util.dao.query.params.UserQueryParameters;

public class UserRestTestUtil extends RestTestUtil {

    private static final String ID          = "id";
    private static final String PAGE        = "page";
    private static final String USERNAME    = "username";

    private UserRestTestUtil() {}

    public static RequestSpecification usernameRequest() {
        final JSONObject json = usernameJson();
        return getRequestJson(json.toString());
    }

    public static RequestSpecification usernameBadRequest() {
        final JSONObject json = badUsernameJson();
        return getRequestJson(json.toString());
    }

    public static RequestSpecification idPageRequest(long id, int page) {
        final JSONObject json = idPageJSON(id, page);
        return getRequestJson(json.toString());
    }

    private static JSONObject usernameJson() {
        final JSONObject json = new JSONObject();
        json.put(USERNAME, UserQueryParameters.USERNAME);
        return json;
    }

    private static JSONObject badUsernameJson() {
        final JSONObject json = new JSONObject();
        json.put(USERNAME, "q");
        return json;
    }

    private static JSONObject idPageJSON(long id, int page) {
        final JSONObject json = new JSONObject();
        json.put(ID, id);
        json.put(PAGE, page);
        return json;
    }
}
