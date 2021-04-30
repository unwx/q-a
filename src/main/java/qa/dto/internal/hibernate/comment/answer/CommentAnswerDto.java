package qa.dto.internal.hibernate.comment.answer;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

public class CommentAnswerDto {

    private final Long commentId;
    private final Date creationDate;
    private final String text;
    private final CommentAnswerAuthorDto author;

    public static final String ID = "c_id";
    public static final String CREATION_DATE = "c_c_date";
    public static final String TEXT = "c_text";

    public CommentAnswerDto(Object[] tuples,
                            Map<String, Integer> aliasToIndexMap) {
        this.commentId = ((BigInteger) tuples[aliasToIndexMap.get(ID)]).longValue();
        this.creationDate = (Date) tuples[aliasToIndexMap.get(CREATION_DATE)];
        this.text = (String) tuples[aliasToIndexMap.get(TEXT)];
        this.author = new CommentAnswerAuthorDto(tuples, aliasToIndexMap);
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

    public CommentAnswerAuthorDto getAuthor() {
        return author;
    }
}
