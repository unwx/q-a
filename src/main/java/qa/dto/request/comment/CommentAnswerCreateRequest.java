package qa.dto.request.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentAnswerCreateRequest {

    @JsonProperty("answer_id")
    private final Long answerId;
    private final String text;

    public CommentAnswerCreateRequest(Long answerId,
                                      String text) {
        this.answerId = answerId;
        this.text = text;
    }

    protected CommentAnswerCreateRequest() {
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
