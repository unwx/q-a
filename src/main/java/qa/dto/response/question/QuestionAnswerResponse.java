package qa.dto.response.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import qa.domain.CommentAnswer;
import qa.domain.User;

import java.util.Date;
import java.util.List;

public class QuestionAnswerResponse {

    @JsonProperty("id")
    private final Long answerId;
    private final String text;
    @JsonProperty("creation_date")
    private final Date creationDate;
    private final Boolean answered;
    private final User author;
    private final List<CommentAnswer> comments;

    public QuestionAnswerResponse(Long answerId,
                                  String text,
                                  Date creationDate,
                                  Boolean answered,
                                  User author,
                                  List<CommentAnswer> comments) {
        this.answerId = answerId;
        this.text = text;
        this.creationDate = creationDate;
        this.answered = answered;
        this.author = author;
        this.comments = comments;
    }

    public QuestionAnswerResponse() {
        this.answerId = null;
        this.text = null;
        this.creationDate = null;
        this.answered = null;
        this.author = null;
        this.comments = null;
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

    public Boolean getAnswered() {
        return answered;
    }

    public User getAuthor() {
        return author;
    }

    public List<CommentAnswer> getComments() {
        return comments;
    }
}
