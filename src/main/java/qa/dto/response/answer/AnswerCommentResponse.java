package qa.dto.response.answer;

import com.fasterxml.jackson.annotation.JsonProperty;
import qa.domain.User;

import java.util.Date;

public class AnswerCommentResponse {

    @JsonProperty("id")
    private final Long answerId;
    private final String text;
    @JsonProperty("creation_date")
    private final Date creationDate;
    private final User author;


    public AnswerCommentResponse(Long answerId,
                                 String text,
                                 Date creationDate,
                                 User author) {
        this.answerId = answerId;
        this.text = text;
        this.creationDate = creationDate;
        this.author = author;
    }

    protected AnswerCommentResponse() {
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
