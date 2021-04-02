package qa.dto.internal.hibernate.question;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

public class QuestionCommentDto {

    private final Long commentId;
    private final String text;
    private final Date creationDate;

    private final QuestionCommentAuthorDto author;

    public static final String ID = "que_c_id";
    public static final String TEXT = "que_c_text";
    public static final String CREATION_DATE = "que_c_c_date";

    public QuestionCommentDto(Object[] tuples,
                              Map<String, Integer> aliasToIndexMap) {
        this.commentId = ((BigInteger) tuples[aliasToIndexMap.get(ID)]).longValue();
        this.text = (String) tuples[aliasToIndexMap.get(TEXT)];
        this.creationDate = (Date) tuples[aliasToIndexMap.get(CREATION_DATE)];
        this.author = new QuestionCommentAuthorDto(tuples, aliasToIndexMap);
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getText() {
        return text;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public QuestionCommentAuthorDto getAuthor() {
        return author;
    }
}
