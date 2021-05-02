package qa.dto.request.answer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerCreateRequest {

    @JsonProperty("question_id")
    private final Long questionId;
    private final String text;

    public AnswerCreateRequest(Long questionId,
                               String text) {
        this.questionId = questionId;
        this.text = text;
    }

    private AnswerCreateRequest() {
        this.questionId = null;
        this.text = null;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getText() {
        return text;
    }
}
