package qa.dto.request.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentAnswerLikeRequest {

    @JsonProperty("id")
    protected final Long commentId;

    public CommentAnswerLikeRequest(Long commentId) {
        this.commentId = commentId;
    }

    private CommentAnswerLikeRequest() {
        this.commentId = null;
    }

    public Long getCommentId() {
        return commentId;
    }
}
