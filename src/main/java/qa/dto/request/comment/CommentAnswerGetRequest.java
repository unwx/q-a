package qa.dto.request.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentAnswerGetRequest {

    @JsonProperty("id")
    private final Long answerId;
    private final Integer page;

    public CommentAnswerGetRequest(Long answerId,
                                   Integer page) {
        this.answerId = answerId;
        this.page = page;
    }

    private CommentAnswerGetRequest() {
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
