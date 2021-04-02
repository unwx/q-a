package qa.dto.response.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import qa.domain.User;

import java.util.Date;

public class QuestionCommentResponse {

    @JsonProperty("id")
    private final Long commentId;
    private final String text;
    @JsonProperty("creation_date")
    private final Date creationDate;
    private final User author;

    public QuestionCommentResponse(Long commentId,
                                   String text,
                                   Date creationDate,
                                   User author) {
        this.commentId = commentId;
        this.text = text;
        this.creationDate = creationDate;
        this.author = author;
    }

    public QuestionCommentResponse() {
        this.commentId = null;
        this.text = null;
        this.creationDate = null;
        this.author = null;
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getText() {
        return text;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public User getAuthor() {
        return author;
    }
}
