package qa.dto.request.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentAnswerDeleteRequest {

    @JsonProperty("id")
    private final Long commentId;

    public CommentAnswerDeleteRequest(Long commentId) {
        this.commentId = commentId;
    }

    protected CommentAnswerDeleteRequest() {
        this.commentId = null;
    }

    public Long getCommentId() {
        return commentId;
    }
}
