package qa.dto.response.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import qa.domain.Answer;
import qa.domain.Comment;
import qa.domain.User;

import java.util.Date;
import java.util.List;

public class QuestionFullResponse {

    @JsonProperty("id")
    private final Long questionId;
    private final String text;
    private final String title;
    private final Date creationDate;
    private final Date lastActivity;
    private final String[] tags;
    private final User author;
    private final List<Answer> answers;
    private final List<Comment> comments;

    public QuestionFullResponse(Long questionId,
                                String text,
                                String title,
                                Date creationDate,
                                Date lastActivity,
                                String[] tags,
                                User author,
                                List<Answer> answers,
                                List<Comment> comments) {
        this.questionId = questionId;
        this.text = text;
        this.title = title;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
        this.tags = tags;
        this.author = author;
        this.answers = answers;
        this.comments = comments;
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

    public List<Comment> getComments() {
        return comments;
    }
}
