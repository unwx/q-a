package qa.util.dao.query.params;

import java.util.Date;

public final class QuestionQueryParameters {

    private QuestionQueryParameters() {
    }

    public static final Date DATE = new Date(99999999999999L);
    public static final String TAGS = "java, spring";
    public static final String[] TAGS_ARRAY = new String[]{"java", "spring"};
    public static final String TITLE = "how default redirect URL work in spring security 5";
    public static final String TEXT =
            """       
             I have gone through below posts. Still I am unable to understand redirection uri concept.
                         
             https://www.baeldung.com/spring-webclient-oauth2
                         
             https://docs.spring.io/spring-security/site/docs/5.0.7.RELEASE/reference
             /html/oauth2login-advanced.html#oauth2login-advanced-redirection-endpoint
                         
             https://docs.spring.io/spring-security/site/docs/5.2.x/reference/html/
             oauth2.html#oauth2login-sample-redirect-uri
                         
             In identity server , we get our client application registered and for code grant type
             , we tell that this would be our redirect url i.e. https://someserver:port/
                         
             However, spring and other post suggests that to set redirect uri as {baseUrl}/login/oauth2/code/{registrationId}.
                         
             I am confused if i have set redirect uri as https://someserver:port/ in identity server
              , how {baseUrl}/login/oauth2/code/{registrationId} i.e. https://someserver:port/login/oauth2/code/
              {registrationId} will work. Should it not give invalid redirect uri?
                         
             Please correct my understanding.
             """;
    public static final String SECOND_TEXT =
            """
            The NullPointerException (NPE) occurs when you declare a variable but did not create an object and assign it to the variable before trying to\s\
            use the contents of the variable (called dereferencing). So you are pointing to something that does not actually exist.\
            """;
}
