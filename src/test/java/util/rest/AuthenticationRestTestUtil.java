package util.rest;

import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import util.dao.query.params.UserQueryParameters;

public class AuthenticationRestTestUtil extends RestTestUtil {

    private static final String EMAIL        = "email";
    private static final String USERNAME     = "username";
    private static final String PASSWORD     = "password";

    private AuthenticationRestTestUtil(){}

    public static RequestSpecification getRegistrationRequest() {
        final JSONObject json = getRegistrationJson();
        return getRequestJson(json.toString());
    }

    public static RequestSpecification getLoginRequest() {
        final JSONObject json = getLoginJson();
        return getRequestJson(json.toString());
    }

    private static JSONObject getRegistrationJson() {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(EMAIL, JwtTestUtil.USER_EMAIL);
        requestParams.put(USERNAME, UserQueryParameters.USERNAME);
        requestParams.put(PASSWORD, JwtTestUtil.USER_PASSWORD);
        return requestParams;
    }

    private static JSONObject getLoginJson() {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(EMAIL, JwtTestUtil.USER_EMAIL);
        requestParams.put(PASSWORD, JwtTestUtil.USER_PASSWORD);
        return requestParams;
    }
}
