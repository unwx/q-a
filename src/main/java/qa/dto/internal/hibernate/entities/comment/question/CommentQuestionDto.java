package qa.dto.internal.hibernate.entities.comment.question;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

public class CommentQuestionDto {

    private final Long commentId;
    private final String text;
    private final Date creationDate;

    private final CommentQuestionAuthorDto author;

    public static final String ID = "c_id";
    public static final String TEXT = "c_text";
    public static final String CREATION_DATE = "c_c_date";

    public CommentQuestionDto(Object[] tuples,
                              Map<String, Integer> aliasToIndexMap) {
        this.commentId = ((BigInteger) tuples[aliasToIndexMap.get(ID)]).longValue();
        this.text = (String) tuples[aliasToIndexMap.get(TEXT)];
        this.creationDate = (Date) tuples[aliasToIndexMap.get(CREATION_DATE)];
        this.author = new CommentQuestionAuthorDto(tuples, aliasToIndexMap);
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

    public CommentQuestionAuthorDto getAuthor() {
        return author;
    }
}
