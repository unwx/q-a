package qa.dto.request.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentQuestionDeleteRequest {

    @JsonProperty("id")
    private final Long commentId;

    public CommentQuestionDeleteRequest(Long commentId) {
        this.commentId = commentId;
    }

    private CommentQuestionDeleteRequest() {
        this.commentId = null;
    }

    public Long getCommentId() {
        return commentId;
    }
}
