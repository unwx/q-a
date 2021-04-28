package qa.serializer.comment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.dto.response.comment.CommentAnswerResponse;

import java.io.IOException;

public class CommentAnswerResponseSerializer extends JsonSerializer<CommentAnswerResponse> {

    private static final CommentFullResponseSerializer serializer = new CommentFullResponseSerializer();

    @Override
    public void serialize(CommentAnswerResponse response, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        serializer.serialize(response, gen, serializerProvider);
    }
}
