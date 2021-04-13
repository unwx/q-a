package qa.serializer.question;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.dto.response.comment.CommentQuestionResponse;
import qa.util.serialization.CommentQuestionSerializationUtil;

import java.io.IOException;

public class QuestionCommentResponseSerializer extends JsonSerializer<CommentQuestionResponse> {
    @Override
    public void serialize(CommentQuestionResponse commentQuestionResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        CommentQuestionSerializationUtil.writeCommentQuestion(commentQuestionResponse, jsonGenerator);
        jsonGenerator.writeEndObject();
    }
}
