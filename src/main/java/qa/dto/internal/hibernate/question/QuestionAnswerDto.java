package qa.dto.internal.hibernate.question;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class QuestionAnswerDto {

    private final Long answerId;
    private final String text;
    private final Date creationDate;
    private final Boolean answered;
    private final QuestionAnswerAuthorDto author;
    private final List<QuestionAnswerCommentDto> comments = new ArrayList<>();

    public static final String ID = "q_a_id";
    public static final String TEXT = "q_a_text";
    public static final String CREATION_DATE = "q_a_c_date";
    public static final String ANSWERED = "q_a_answered";

    public QuestionAnswerDto(Object[] tuples,
                             Map<String, Integer> aliasToIndexMap) {
        this.answerId = ((BigInteger) tuples[aliasToIndexMap.get(ID)]).longValue();
        this.text = (String) tuples[aliasToIndexMap.get(TEXT)];
        this.creationDate = (Date) tuples[aliasToIndexMap.get(CREATION_DATE)];
        this.answered = (Boolean) tuples[aliasToIndexMap.get(ANSWERED)];
        this.author = new QuestionAnswerAuthorDto(tuples, aliasToIndexMap);
        comments.add(new QuestionAnswerCommentDto(tuples, aliasToIndexMap));
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

    public QuestionAnswerAuthorDto getAuthor() {
        return author;
    }

    public List<QuestionAnswerCommentDto> getComments() {
        return comments;
    }
}
