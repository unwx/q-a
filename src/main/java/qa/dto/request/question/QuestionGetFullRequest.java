package qa.dto.request.question;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionGetFullRequest {

    @JsonProperty("id")
    private final Long questionId;

    public QuestionGetFullRequest(Long questionId) {
        this.questionId = questionId;
    }

    public Long getQuestionId() {
        return questionId;
    }
}
