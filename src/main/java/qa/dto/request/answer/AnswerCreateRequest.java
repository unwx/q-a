package qa.dto.request.answer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerCreateRequest {

    @JsonProperty("question_id")
    private final Long id;
    private final String text;

    public AnswerCreateRequest(Long id,
                               String text) {
        this.id = id;
        this.text = text;
    }

    public AnswerCreateRequest() {
        this.id = null;
        this.text = null;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
