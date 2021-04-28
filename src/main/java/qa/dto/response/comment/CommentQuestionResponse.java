package qa.dto.response.comment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import qa.domain.User;
import qa.serializer.comment.CommentQuestionResponseSerializer;

import java.util.Date;

@JsonSerialize(using = CommentQuestionResponseSerializer.class)
public class CommentQuestionResponse extends CommentFullResponse {

    public CommentQuestionResponse(Long commentId,
                                 String text,
                                 Date creationDate,
                                 User author,
                                 int likes,
                                 boolean liked) {
        super(commentId, text, creationDate, author, likes, liked);
    }

    protected CommentQuestionResponse() {}
}
