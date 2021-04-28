package qa.dto.request.question;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionLikeRequest {

    @JsonProperty("id")
    protected final Long questionId;

    public QuestionLikeRequest(Long questionId) {
        this.questionId = questionId;
    }

    private QuestionLikeRequest() {
        this.questionId = null;
    }

    public Long getQuestionId() {
        return questionId;
    }
}
