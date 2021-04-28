package qa.dto.response.answer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import qa.domain.CommentAnswer;
import qa.domain.User;
import qa.serializer.answer.AnswerFullResponseSerializer;

import java.util.Date;
import java.util.List;

@JsonSerialize(using = AnswerFullResponseSerializer.class)
public class AnswerFullResponse {

    @JsonProperty("id")
    private final Long answerId;
    private final String text;
    @JsonProperty("creation_date")
    private final Date creationDate;
    private final Boolean answered;
    private final User author;
    private final List<CommentAnswer> comments;
    private final int likes;
    private final boolean liked;

    public AnswerFullResponse(Long answerId,
                              String text,
                              Date creationDate,
                              Boolean answered,
                              User author,
                              List<CommentAnswer> comments,
                              int likes,
                              boolean liked) {
        this.answerId = answerId;
        this.text = text;
        this.creationDate = creationDate;
        this.answered = answered;
        this.author = author;
        this.comments = comments;
        this.likes = likes;
        this.liked = liked;
    }

    protected AnswerFullResponse() {
        this.answerId = null;
        this.text = null;
        this.creationDate = null;
        this.answered = null;
        this.author = null;
        this.comments = null;
        this.likes = -1;
        this.liked = false;
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

    public int getLikes() {
        return likes;
    }

    public boolean isLiked() {
        return liked;
    }
}
