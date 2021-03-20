package qa.source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
@PropertySources({
        @PropertySource("classpath:properties/validation/validAuthentication.properties"),
        @PropertySource("classpath:properties/validation/validUser.properties"),
        @PropertySource("classpath:properties/validation/validAnswer.properties"),
        @PropertySource("classpath:properties/validation/validQuestion.properties"),
        @PropertySource("classpath:properties/validation/validComment.properties")}
)
public class ValidationPropertyDataSource {

    /* authentication validation properties */

    @Value("${authentication.password.length.max}")
    private Integer AUTHENTICATION_PASSWORD_LENGTH_MAX;

    @Value("${authentication.password.length.min}")
    private Integer AUTHENTICATION_PASSWORD_LENGTH_MIN;

    /* user validation properties */

    @Value("${user.username.length.max}")
    private Integer USER_USERNAME_LENGTH_MAX;

    @Value("${user.username.length.min}")
    private Integer USER_USERNAME_LENGTH_MIN;

    @Value("${user.username.regexp}")
    private String USER_USERNAME_REGEXP;

    @Value("${user.about.length.max}")
    private Integer USER_ABOUT_LENGTH_MAX;

    @Value("${user.about.length.min}")
    private Integer USER_ABOUT_LENGTH_MIN;

    /* question validation properties */

    @Value("${question.tags.count.min}")
    private Integer QUESTION_TAGS_COUNT_MIN;

    @Value("${question.tags.count.max}")
    private Integer QUESTION_TAGS_COUNT_MAX;

    @Value("${question.tag.length.min}")
    private Integer QUESTION_TAG_LENGTH_MIN;

    @Value("${question.tag.length.max}")
    private Integer QUESTION_TAG_LENGTH_MAX;

    @Value("${question.tag.regexp}")
    private String QUESTION_TAG_REGEXP;

    @Value("${question.title.length.min}")
    private Integer QUESTION_TITLE_LENGTH_MIN;

    @Value("${question.title.length.max}")
    private Integer QUESTION_TITLE_LENGTH_MAX;

    @Value("${question.title.regexp}")
    private String QUESTION_TITLE_REGEXP;

    @Value("${question.text.length.max}")
    private Integer QUESTION_TEXT_LENGTH_MAX;

    @Value("${question.text.length.min}")
    private Integer QUESTION_TEXT_LENGTH_MIN;

    /* answer validation properties */

    @Value("${answer.text.length.max}")
    private Integer ANSWER_TEXT_LENGTH_MAX;

    @Value("${answer.text.length.min}")
    private Integer ANSWER_TEXT_LENGTH_MIN;

    /* comment validation properties */

    @Value("${comment.text.length.max}")
    private Integer COMMENT_TEXT_LENGTH_MAX;

    @Value("${comment.text.length.min}")
    private Integer COMMENT_TEXT_LENGTH_MIN;


    public Integer getAUTHENTICATION_PASSWORD_LENGTH_MAX() {
        return AUTHENTICATION_PASSWORD_LENGTH_MAX;
    }

    public Integer getAUTHENTICATION_PASSWORD_LENGTH_MIN() {
        return AUTHENTICATION_PASSWORD_LENGTH_MIN;
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

    public Integer getQUESTION_TAGS_COUNT_MIN() {
        return QUESTION_TAGS_COUNT_MIN;
    }

    public Integer getQUESTION_TAGS_COUNT_MAX() {
        return QUESTION_TAGS_COUNT_MAX;
    }

    public String getQUESTION_TITLE_REGEXP() {
        return QUESTION_TITLE_REGEXP;
    }

    public Integer getQUESTION_TAG_LENGTH_MIN() {
        return QUESTION_TAG_LENGTH_MIN;
    }

    public Integer getQUESTION_TAG_LENGTH_MAX() {
        return QUESTION_TAG_LENGTH_MAX;
    }

    public String getQUESTION_TAG_REGEXP() {
        return QUESTION_TAG_REGEXP;
    }

    public Integer getQUESTION_TITLE_LENGTH_MIN() {
        return QUESTION_TITLE_LENGTH_MIN;
    }

    public Integer getQUESTION_TITLE_LENGTH_MAX() {
        return QUESTION_TITLE_LENGTH_MAX;
    }

    public Integer getQUESTION_TEXT_LENGTH_MAX() {
        return QUESTION_TEXT_LENGTH_MAX;
    }

    public Integer getQUESTION_TEXT_LENGTH_MIN() {
        return QUESTION_TEXT_LENGTH_MIN;
    }

    public Integer getANSWER_TEXT_LENGTH_MAX() {
        return ANSWER_TEXT_LENGTH_MAX;
    }

    public Integer getANSWER_TEXT_LENGTH_MIN() {
        return ANSWER_TEXT_LENGTH_MIN;
    }

    public Integer getCOMMENT_TEXT_LENGTH_MAX() {
        return COMMENT_TEXT_LENGTH_MAX;
    }

    public Integer getCOMMENT_TEXT_LENGTH_MIN() {
        return COMMENT_TEXT_LENGTH_MIN;
    }
}
