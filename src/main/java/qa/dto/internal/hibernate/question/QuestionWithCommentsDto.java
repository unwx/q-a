package qa.dto.internal.hibernate.question;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class QuestionWithCommentsDto {

    private final String title;
    private final String text;
    private final String tags;
    private final Date creationDate;
    private final Date lastActivity;

    private final QuestionAuthorDto author;

    private final List<QuestionCommentDto> comments = new ArrayList<>();

    public static final String TITLE = "que_title";
    public static final String TEXT = "que_text";
    public static final String TAGS = "que_tags";
    public static final String CREATION_DATE = "que_c_date";
    public static final String LAST_ACTIVITY = "que_l_activity";

    public QuestionWithCommentsDto(Object[] tuples,
                           Map<String, Integer> aliasToIndexMap) {
        this.title = (String) tuples[aliasToIndexMap.get(TITLE)];
        this.text = (String) tuples[aliasToIndexMap.get(TEXT)];
        this.tags = (String) tuples[aliasToIndexMap.get(TAGS)];
        this.creationDate = (Date) tuples[aliasToIndexMap.get(CREATION_DATE)];
        this.lastActivity = (Date) tuples[aliasToIndexMap.get(LAST_ACTIVITY)];
        this.author = new QuestionAuthorDto(tuples, aliasToIndexMap);
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
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

    public QuestionAuthorDto getAuthor() {
        return author;
    }

    public List<QuestionCommentDto> getComments() {
        return comments;
    }
}
