package qa.serializer.comment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.domain.User;
import qa.dto.response.comment.CommentFullResponse;
import qa.serializer.util.DateSerializationUtil;

import java.io.IOException;

public class CommentFullResponseSerializer {

    private static final CommentSerializer commentSerializer = new CommentSerializer();

    public void serialize(CommentFullResponse response, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        final long id = response.getCommentId();
        final int likes = response.getLikes();
        final boolean liked = response.isLiked();
        final String text = response.getText();
        final String creationDate = DateSerializationUtil.dateToString(response.getCreationDate());
        final User author = response.getAuthor();

        commentSerializer.serialize(id, likes, liked, text, creationDate, author, gen, serializerProvider);
    }
}
