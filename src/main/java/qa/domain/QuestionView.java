package qa.domain;

import java.util.Date;

public class QuestionView {

    private final Long questionId;
    private final String title;
    private final String tags;
    private final Date creationDate;
    private final Date lastActivity;
    private final Integer answersCount;
    private final User author;

    public QuestionView(Long questionId,
                        String title,
                        String tags,
                        Date creationDate,
                        Date lastActivity,
                        Integer answersCount,
                        User author) {
        this.questionId = questionId;
        this.title = title;
        this.tags = tags;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
        this.answersCount = answersCount;
        this.author = author;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getTitle() {
        return title;
    }

    public String getTags() {
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

    public User getAuthor() {
        return author;
    }
}
