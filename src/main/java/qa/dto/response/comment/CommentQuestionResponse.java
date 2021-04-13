package qa.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import qa.domain.User;
import qa.serializer.question.QuestionCommentResponseSerializer;

import java.util.Date;

@JsonSerialize(using = QuestionCommentResponseSerializer.class)
public class CommentQuestionResponse {

    @JsonProperty("id")
    private final Long commentId;
    private final String text;
    @JsonProperty("creation_date")
    private final Date creationDate;
    private final User author;

    public CommentQuestionResponse(Long commentId,
                                   String text,
                                   Date creationDate,
                                   User author) {
        this.commentId = commentId;
        this.text = text;
        this.creationDate = creationDate;
        this.author = author;
    }

    public CommentQuestionResponse() {
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
