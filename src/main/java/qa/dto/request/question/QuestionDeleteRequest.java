package qa.dto.request.question;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionDeleteRequest {

    @JsonProperty("id")
    private final Long questionId;

    public QuestionDeleteRequest(Long questionId) {
        this.questionId = questionId;
    }

    private QuestionDeleteRequest() {
        this.questionId = null;
    }

    public Long getQuestionId() {
        return questionId;
    }
}
