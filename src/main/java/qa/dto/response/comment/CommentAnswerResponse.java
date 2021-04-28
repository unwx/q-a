package qa.dto.response.comment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import qa.domain.User;
import qa.serializer.comment.CommentAnswerResponseSerializer;

import java.util.Date;

@JsonSerialize(using = CommentAnswerResponseSerializer.class)
public class CommentAnswerResponse extends CommentFullResponse {

    public CommentAnswerResponse(Long commentId,
                                 String text,
                                 Date creationDate,
                                 User author,
                                 int likes,
                                 boolean liked) {
        super(commentId, text, creationDate, author, likes, liked);
    }

    protected CommentAnswerResponse() {}
}
