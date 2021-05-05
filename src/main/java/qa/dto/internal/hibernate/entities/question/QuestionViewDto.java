package qa.dto.internal.hibernate.entities.question;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

public class QuestionViewDto {

    private final Long questionId;
    private final String title;
    private final String tags;
    private final Date creationDate;
    private final Date lastActivity;
    private final Integer answersCount;

    private final QuestionAuthorDto author;

    public static final String ID               = "que_id";
    public static final String TITLE            = "que_title";
    public static final String TAGS             = "que_tags";
    public static final String CREATION_DATE    = "que_c_date";
    public static final String LAST_ACTIVITY    = "que_l_activity";
    public static final String ANSWERS_COUNT    = "que_a_count";

    public QuestionViewDto(Object[] tuples,
                           Map<String, Integer> aliasToIndexMap) {
        this.questionId = ((BigInteger) tuples[aliasToIndexMap.get(ID)]).longValue();
        this.title = (String) tuples[aliasToIndexMap.get(TITLE)];
        this.tags = (String) tuples[aliasToIndexMap.get(TAGS)];
        this.creationDate = (Date) tuples[aliasToIndexMap.get(CREATION_DATE)];
        this.lastActivity = (Date) tuples[aliasToIndexMap.get(LAST_ACTIVITY)];
        this.author = new QuestionAuthorDto(tuples, aliasToIndexMap);

        BigInteger temp = (BigInteger) tuples[aliasToIndexMap.get(ANSWERS_COUNT)];
        this.answersCount = temp == null ? 0 : temp.intValue();
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

    public QuestionAuthorDto getAuthor() {
        return author;
    }
}
