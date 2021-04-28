package qa.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import qa.domain.User;

import java.util.Date;

public abstract class CommentFullResponse {
    @JsonProperty("id")
    private final Long commentId;
    private final String text;
    @JsonProperty("creation_date")
    private final Date creationDate;
    private final User author;
    private final int likes;
    private final boolean liked;

    protected CommentFullResponse(Long commentId,
                               String text,
                               Date creationDate,
                               User author,
                               int likes,
                               boolean liked) {
        this.commentId = commentId;
        this.text = text;
        this.creationDate = creationDate;
        this.author = author;
        this.likes = likes;
        this.liked = liked;
    }

    protected CommentFullResponse() {
        this.commentId = null;
        this.text = null;
        this.creationDate = null;
        this.author = null;
        this.likes = -1;
        this.liked = false;
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

    public int getLikes() {
        return likes;
    }

    public boolean isLiked() {
        return liked;
    }
}
