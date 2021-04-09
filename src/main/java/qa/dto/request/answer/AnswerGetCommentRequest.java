package qa.dto.request.answer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerGetCommentRequest {

    @JsonProperty("id")
    private final Long answerId;
    private final Integer page;

    public AnswerGetCommentRequest(Long answerId,
                                   Integer page) {
        this.answerId = answerId;
        this.page = page;
    }

    protected AnswerGetCommentRequest() {
        this.answerId = null;
        this.page = null;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public Integer getPage() {
        return page;
    }
}
