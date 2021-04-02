package qa.dto.internal.hibernate.answer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AnswerFullDto {

    private final Long answerId;
    private final String text;
    private final Date creationDate;
    private final Boolean answered;
    private final AnswerAuthorDto author;
    private final List<AnswerCommentDto> comments = new ArrayList<>();

    public static final String ID = "ans_id";
    public static final String TEXT = "ans_text";
    public static final String CREATION_DATE = "ans_c_date";
    public static final String ANSWERED = "ans_answered";

    public AnswerFullDto(Object[] tuples,
                         Map<String, Integer> aliasToIndexMap) {
        this.answerId = ((BigInteger) tuples[aliasToIndexMap.get(ID)]).longValue();
        this.text = (String) tuples[aliasToIndexMap.get(TEXT)];
        this.creationDate = (Date) tuples[aliasToIndexMap.get(CREATION_DATE)];
        this.answered = (Boolean) tuples[aliasToIndexMap.get(ANSWERED)];
        this.author = new AnswerAuthorDto(tuples, aliasToIndexMap);
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

    public AnswerAuthorDto getAuthor() {
        return author;
    }

    public List<AnswerCommentDto> getComments() {
        return comments;
    }
}
