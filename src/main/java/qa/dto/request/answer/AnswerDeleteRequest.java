package qa.dto.request.answer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerDeleteRequest {

    @JsonProperty("id")
    private final Long answerId;

    public AnswerDeleteRequest(Long answerId) {
        this.answerId = answerId;
    }

    private AnswerDeleteRequest() {
        this.answerId = null;
    }

    public Long getAnswerId() {
        return answerId;
    }
}
