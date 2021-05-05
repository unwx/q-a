package qa.source.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:properties/validation/validUser.properties")
public class UserPropertyDataSource {

    private final Integer USER_USERNAME_LENGTH_MAX;
    private final Integer USER_USERNAME_LENGTH_MIN;
    private final String USER_USERNAME_REGEXP;
    private final Integer USER_ABOUT_LENGTH_MAX;
    private final Integer USER_ABOUT_LENGTH_MIN;

    private UserPropertyDataSource(@Value("${user.username.length.max}") Integer user_username_length_max,
                                   @Value("${user.username.length.min}") Integer user_username_length_min,
                                   @Value("${user.username.regexp}") String user_username_regexp,
                                   @Value("${user.about.length.max}") Integer user_about_length_max,
                                   @Value("${user.about.length.min}") Integer user_about_length_min) {
        USER_USERNAME_LENGTH_MAX = user_username_length_max;
        USER_USERNAME_LENGTH_MIN = user_username_length_min;
        USER_USERNAME_REGEXP = user_username_regexp;
        USER_ABOUT_LENGTH_MAX = user_about_length_max;
        USER_ABOUT_LENGTH_MIN = user_about_length_min;
    }

    public Integer getUSER_USERNAME_LENGTH_MAX() {
        return USER_USERNAME_LENGTH_MAX;
    }

    public Integer getUSER_USERNAME_LENGTH_MIN() {
        return USER_USERNAME_LENGTH_MIN;
    }

    public String getUSER_USERNAME_REGEXP() {
        return USER_USERNAME_REGEXP;
    }

    public Integer getUSER_ABOUT_LENGTH_MAX() {
        return USER_ABOUT_LENGTH_MAX;
    }

    public Integer getUSER_ABOUT_LENGTH_MIN() {
        return USER_ABOUT_LENGTH_MIN;
    }
}
