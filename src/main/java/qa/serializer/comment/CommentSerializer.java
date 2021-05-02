package qa.serializer.comment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.domain.Comment;
import qa.domain.User;
import qa.serializer.user.AuthorSerializer;
import qa.serializer.util.DateSerializationUtil;

import java.io.IOException;
import java.util.List;

public class CommentSerializer extends JsonSerializer<List<? extends Comment>> {

    private static final String ID = "id";
    private static final String TEXT = "text";
    private static final String CREATION_DATE = "creation_date";
    private static final String LIKES = "likes";
    private static final String LIKED = "liked";

    private static final AuthorSerializer authorSerializer = new AuthorSerializer();

    @Override
    public void serialize(List<? extends Comment> comments, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeArrayFieldStart("comments");
        for (Comment c : comments)
            this.process(c, gen, serializerProvider);
        gen.writeEndArray();
    }

    public void serialize(long id,
                        int likes,
                        boolean liked,
                        String text,
                        String creation_date,
                        User author,
                        JsonGenerator gen,
                        SerializerProvider serializerProvider) throws IOException {

        gen.writeStartObject();
        gen.writeNumberField(ID, id);
        gen.writeStringField(TEXT, text);
        gen.writeStringField(CREATION_DATE, creation_date);
        gen.writeNumberField(LIKES, likes);
        gen.writeBooleanField(LIKED, liked);

        authorSerializer.serialize(author, gen, serializerProvider);
        gen.writeEndObject();
    }

    private void process(Comment comment, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        final long id = comment.getId();
        final int likes = comment.getLikes();
        final boolean liked = comment.isLiked();
        final String text = comment.getText();
        final String creation_date = DateSerializationUtil.dateToString(comment.getCreationDate());
        final User author = comment.getAuthor();

        this.serialize(id, likes, liked, text, creation_date, author, gen, serializerProvider);
    }
}
