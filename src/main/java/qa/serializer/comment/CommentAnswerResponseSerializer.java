package qa.serializer.comment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.dto.response.comment.CommentAnswerResponse;
import qa.util.serialization.CommentQuestionSerializationUtil;

import java.io.IOException;

public class CommentAnswerResponseSerializer extends JsonSerializer<CommentAnswerResponse> {
    @Override
    public void serialize(CommentAnswerResponse commentAnswerResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        CommentQuestionSerializationUtil.writeCommentAnswer(commentAnswerResponse, jsonGenerator);
        jsonGenerator.writeEndObject();
    }
}
