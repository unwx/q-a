package qa.dto.request.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentAnswerEditRequest {

    @JsonProperty("id")
    private final Long commentId;
    private final String text;

    public CommentAnswerEditRequest(Long commentId,
                                    String text) {
        this.commentId = commentId;
        this.text = text;
    }

    protected CommentAnswerEditRequest() {
        this.commentId = null;
        this.text = null;
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getText() {
        return text;
    }
}
