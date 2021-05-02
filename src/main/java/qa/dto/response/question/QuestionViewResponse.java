package qa.dto.response.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import qa.domain.User;
import qa.serializer.question.QuestionViewResponseSerializer;

import java.util.Date;

@JsonSerialize(using = QuestionViewResponseSerializer.class)
public class QuestionViewResponse {

    @JsonProperty("id")
    private final Long questionId;
    private final String title;
    private final String[] tags;
    @JsonProperty("creation_date")
    private final Date creationDate;
    @JsonProperty("last_activity")
    private final Date lastActivity;
    @JsonProperty("answers_count")
    private final Integer answersCount;
    @JsonProperty("author")
    private final User user;

    public QuestionViewResponse(Long questionId,
                                String title,
                                String[] tags,
                                Date creationDate,
                                Date lastActivity,
                                Integer answersCount,
                                User user) {
        this.questionId = questionId;
        this.title = title;
        this.tags = tags;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
        this.answersCount = answersCount;
        this.user = user;
    }

    private QuestionViewResponse() {
        this.questionId = null;
        this.title = null;
        this.tags = null;
        this.creationDate = null;
        this.lastActivity = null;
        this.answersCount = null;
        this.user = null;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getTitle() {
        return title;
    }

    public String[] getTags() {
        return tags;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public Integer getAnswersCount() {
        return answersCount;
    }

    public User getUser() {
        return user;
    }
}
