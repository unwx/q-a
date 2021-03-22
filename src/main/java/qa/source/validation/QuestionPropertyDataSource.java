package qa.source.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:properties/validation/validQuestion.properties")
public class QuestionPropertyDataSource {

    private final Integer QUESTION_TAGS_COUNT_MIN;
    private final Integer QUESTION_TAGS_COUNT_MAX;
    private final Integer QUESTION_TAG_LENGTH_MIN;
    private final Integer QUESTION_TAG_LENGTH_MAX;
    private final String QUESTION_TAG_REGEXP;
    private final Integer QUESTION_TITLE_LENGTH_MIN;
    private final Integer QUESTION_TITLE_LENGTH_MAX;
    private final Integer QUESTION_TEXT_LENGTH_MAX;
    private final Integer QUESTION_TEXT_LENGTH_MIN;

    private QuestionPropertyDataSource(@Value("${question.tags.count.min}") Integer question_tags_count_min,
                                      @Value("${question.tags.count.max}") Integer question_tags_count_max,
                                      @Value("${question.tag.length.min}") Integer question_tag_length_min,
                                      @Value("${question.tag.length.max}") Integer question_tag_length_max,
                                      @Value("${question.tag.regexp}") String question_tag_regexp,
                                      @Value("${question.title.length.min}") Integer question_title_length_min,
                                      @Value("${question.title.length.max}") Integer question_title_length_max,
                                      @Value("${question.text.length.max}") Integer question_text_length_max,
                                      @Value("${question.text.length.min}") Integer question_text_length_min) {
        QUESTION_TAGS_COUNT_MIN = question_tags_count_min;
        QUESTION_TAGS_COUNT_MAX = question_tags_count_max;
        QUESTION_TAG_LENGTH_MIN = question_tag_length_min;
        QUESTION_TAG_LENGTH_MAX = question_tag_length_max;
        QUESTION_TAG_REGEXP = question_tag_regexp;
        QUESTION_TITLE_LENGTH_MIN = question_title_length_min;
        QUESTION_TITLE_LENGTH_MAX = question_title_length_max;
        QUESTION_TEXT_LENGTH_MAX = question_text_length_max;
        QUESTION_TEXT_LENGTH_MIN = question_text_length_min;
    }

    public Integer getQUESTION_TAGS_COUNT_MIN() {
        return QUESTION_TAGS_COUNT_MIN;
    }

    public Integer getQUESTION_TAGS_COUNT_MAX() {
        return QUESTION_TAGS_COUNT_MAX;
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
}
