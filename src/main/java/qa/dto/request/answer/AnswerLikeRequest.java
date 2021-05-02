package qa.dto.request.answer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerLikeRequest {

    @JsonProperty("id")
    private final Long answerId;

    public AnswerLikeRequest(Long answerId) {
        this.answerId = answerId;
    }

    private AnswerLikeRequest() {
        this.answerId = null;
    }

    public Long getAnswerId() {
        return answerId;
    }
}