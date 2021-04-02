package qa.dto.internal.hibernate.answer;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

public class AnswerCommentDto {

    private final Long commentId;
    private final Date creationDate;
    private final String text;
    private final AnswerCommentAuthorDto author;

    public static final String ID = "ans_c_id";
    public static final String CREATION_DATE = "ans_c_c_date";
    public static final String TEXT = "ans_c_text";

    public AnswerCommentDto(Object[] tuples,
                            Map<String, Integer> aliasToIndexMap) {
        this.commentId = ((BigInteger) tuples[aliasToIndexMap.get(ID)]).longValue();
        this.creationDate = (Date) tuples[aliasToIndexMap.get(CREATION_DATE)];
        this.text = (String) tuples[aliasToIndexMap.get(TEXT)];
        this.author = new AnswerCommentAuthorDto(tuples, aliasToIndexMap);
    }

    public Long getCommentId() {
        return commentId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getText() {
        return text;
    }

    public AnswerCommentAuthorDto getAuthor() {
        return author;
    }
}
