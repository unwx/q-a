package util.rest;

import org.json.JSONObject;
import util.dao.query.params.UserQueryParameters;

public class AuthenticationRestTestUtil extends RestTestUtil {

    private AuthenticationRestTestUtil(){
    }

    public static JSONObject getRegistrationJson() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", JwtTestUtil.USER_EMAIL);
        requestParams.put("username", UserQueryParameters.USERNAME);
        requestParams.put("password", JwtTestUtil.USER_PASSWORD);
        return requestParams;
    }

    public static JSONObject getLoginJson() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", JwtTestUtil.USER_EMAIL);
        requestParams.put("password", JwtTestUtil.USER_PASSWORD);
        return requestParams;
    }
}
