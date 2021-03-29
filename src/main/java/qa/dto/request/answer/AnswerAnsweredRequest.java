package qa.dto.request.answer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerAnsweredRequest {

    @JsonProperty("id")
    private final Long answerId;

    public AnswerAnsweredRequest(Long answerId) {
        this.answerId = answerId;
    }

    protected AnswerAnsweredRequest() {
        this.answerId = null;
    }

    public Long getAnswerId() {
        return answerId;
    }
}
