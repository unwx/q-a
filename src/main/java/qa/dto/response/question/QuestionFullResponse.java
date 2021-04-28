package qa.dto.response.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import qa.domain.Answer;
import qa.domain.CommentQuestion;
import qa.domain.User;
import qa.serializer.question.QuestionFullResponseSerializer;

import java.util.Date;
import java.util.List;

@JsonSerialize(using = QuestionFullResponseSerializer.class)
public class QuestionFullResponse {

    @JsonProperty("id")
    private final Long questionId;
    private final String text;
    private final String title;
    @JsonProperty("creation_date")
    private final Date creationDate;
    @JsonProperty("last_activity")
    private final Date lastActivity;
    private final String[] tags;
    private final User author;
    private final List<Answer> answers;
    private final List<CommentQuestion> comments;
    private final int likes;
    private final boolean liked;

    public QuestionFullResponse(Long questionId,
                                String text,
                                String title,
                                Date creationDate,
                                Date lastActivity,
                                String[] tags,
                                User author,
                                List<Answer> answers,
                                List<CommentQuestion> comments,
                                int likes,
                                boolean liked) {
        this.questionId = questionId;
        this.text = text;
        this.title = title;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
        this.tags = tags;
        this.author = author;
        this.answers = answers;
        this.comments = comments;
        this.likes = likes;
        this.liked = liked;
    }

    protected QuestionFullResponse() {
        this.questionId = null;
        this.text = null;
        this.title = null;
        this.creationDate = null;
        this.lastActivity = null;
        this.tags = null;
        this.author = null;
        this.answers = null;
        this.comments = null;
        this.likes = -1;
        this.liked = false;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public String[] getTags() {
        return tags;
    }

    public User getAuthor() {
        return author;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public List<CommentQuestion> getComments() {
        return comments;
    }

    public int getLikes() {
        return likes;
    }

    public boolean isLiked() {
        return liked;
    }
}
