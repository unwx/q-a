package qa.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import qa.domain.User;

import java.util.Date;

public class CommentAnswerResponse {

    @JsonProperty("id")
    private final Long answerId;
    private final String text;
    @JsonProperty("creation_date")
    private final Date creationDate;
    private final User author;


    public CommentAnswerResponse(Long answerId,
                                 String text,
                                 Date creationDate,
                                 User author) {
        this.answerId = answerId;
        this.text = text;
        this.creationDate = creationDate;
        this.author = author;
    }

    protected CommentAnswerResponse() {
        this.answerId = null;
        this.text = null;
        this.creationDate = null;
        this.author = null;
    }

    public Long getAnswerId() {
        return answerId;
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
