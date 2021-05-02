package qa.dto.request.answer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerEditRequest {

    @JsonProperty("id")
    private final Long answerId;
    private final String text;

    public AnswerEditRequest(Long answerId,
                             String text) {
        this.answerId = answerId;
        this.text = text;
    }

    private AnswerEditRequest() {
        this.answerId = null;
        this.text = null;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public String getText() {
        return text;
    }
}
