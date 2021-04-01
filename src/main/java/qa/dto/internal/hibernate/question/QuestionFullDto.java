package qa.dto.internal.hibernate.question;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class QuestionFullDto {

    private final String title;
    private final String text;
    private final String tags;
    private final Date creationDate;
    private final Date lastActivity;

    private final QuestionAuthorDto author;

    private final List<QuestionCommentDto> comments = new ArrayList<>();
    private final List<QuestionAnswerDto> answers = new ArrayList<>();

    public static final String TITLE = "q_title";
    public static final String TEXT = "q_text";
    public static final String TAGS = "q_tags";
    public static final String CREATION_DATE = "q_c_date";
    public static final String LAST_ACTIVITY = "q_l_activity";

    public QuestionFullDto(Object[] tuples,
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

    public List<QuestionAnswerDto> getAnswers() {
        return answers;
    }
}
