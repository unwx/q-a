package qa.source.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:properties/validation/validComment.properties")
public class CommentPropertyDataSource {

    private final Integer COMMENT_TEXT_LENGTH_MAX;
    private final Integer COMMENT_TEXT_LENGTH_MIN;

    private CommentPropertyDataSource(@Value("${comment.text.length.max}") Integer comment_text_length_max,
                                      @Value("${comment.text.length.min}") Integer comment_text_length_min) {
        COMMENT_TEXT_LENGTH_MAX = comment_text_length_max;
        COMMENT_TEXT_LENGTH_MIN = comment_text_length_min;
    }

    public Integer getCOMMENT_TEXT_LENGTH_MAX() {
        return COMMENT_TEXT_LENGTH_MAX;
    }

    public Integer getCOMMENT_TEXT_LENGTH_MIN() {
        return COMMENT_TEXT_LENGTH_MIN;
    }
}
