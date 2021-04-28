package qa.dto.request.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentQuestionLikeRequest {

    @JsonProperty("id")
    protected final Long commentId;

    public CommentQuestionLikeRequest(Long commentId) {
        this.commentId = commentId;
    }

    private CommentQuestionLikeRequest() {
        this.commentId = null;
    }

    public Long getCommentId() {
        return commentId;
    }
}
