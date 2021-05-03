package qa.serializer.answer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.User;
import qa.serializer.comment.CommentSerializer;
import qa.serializer.user.AuthorSerializer;
import qa.serializer.util.DateSerializationUtil;

import java.io.IOException;
import java.util.List;

public class AnswerSerializer extends JsonSerializer<List<Answer>> {

    private static final String ID              = "id";
    private static final String TEXT            = "text";
    private static final String ANSWERED        = "answered";
    private static final String CREATION_DATE   = "creation_date";
    private static final String LIKES           = "likes";
    private static final String LIKED           = "liked";

    private static final AuthorSerializer authorSerializer = new AuthorSerializer();
    private static final CommentSerializer commentSerializer = new CommentSerializer();

    @Override
    public void serialize(List<Answer> answers, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeArrayFieldStart("answers");
        for (Answer answer : answers) {
            this.process(answer, gen, serializerProvider);
        }
        gen.writeEndArray();
    }

    public void serialize(long id,
                          int likes,
                          boolean liked,
                          boolean answered,
                          String text,
                          String creationDate,
                          User author,
                          List<CommentAnswer> comments,
                          JsonGenerator gen,
                          SerializerProvider serializerProvider) throws IOException {

        gen.writeStartObject();
        gen.writeNumberField(ID, id);
        gen.writeStringField(TEXT, text);
        gen.writeBooleanField(ANSWERED, answered);
        gen.writeStringField(CREATION_DATE, creationDate);
        gen.writeNumberField(LIKES, likes);
        gen.writeBooleanField(LIKED, liked);

        authorSerializer.serialize(author, gen, serializerProvider);
        commentSerializer.serialize(comments, gen, serializerProvider);
        gen.writeEndObject();
    }

    private void process(Answer answer, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        final long id = answer.getId();
        final int likes = answer.getLikes();
        final boolean liked = answer.isLiked();
        final boolean answered = answer.getAnswered();
        final String text = answer.getText();
        final String creationDate = DateSerializationUtil.dateToString(answer.getCreationDate());

        final User author = answer.getAuthor();
        final List<CommentAnswer> comments = answer.getComments();

        this.serialize(id, likes, liked, answered, text, creationDate, author, comments, gen, serializerProvider);
    }
}
