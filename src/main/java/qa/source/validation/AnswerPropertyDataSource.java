package qa.source.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:properties/validation/validAnswer.properties")
public class AnswerPropertyDataSource {

    private final Integer ANSWER_TEXT_LENGTH_MAX;
    private final Integer ANSWER_TEXT_LENGTH_MIN;

    private AnswerPropertyDataSource(@Value("${answer.text.length.max}") Integer answer_text_length_max,
                                     @Value("${answer.text.length.min}") Integer answer_text_length_min) {
        ANSWER_TEXT_LENGTH_MAX = answer_text_length_max;
        ANSWER_TEXT_LENGTH_MIN = answer_text_length_min;
    }

    public Integer getANSWER_TEXT_LENGTH_MAX() {
        return ANSWER_TEXT_LENGTH_MAX;
    }

    public Integer getANSWER_TEXT_LENGTH_MIN() {
        return ANSWER_TEXT_LENGTH_MIN;
    }
}
