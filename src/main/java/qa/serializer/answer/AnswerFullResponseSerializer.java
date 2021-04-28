package qa.serializer.answer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.dto.response.answer.AnswerFullResponse;
import qa.util.serialization.AuthorSerializationUtil;
import qa.util.serialization.CommentAnswerSerializationUtil;
import qa.util.serialization.DateSerializationUtil;

import java.io.IOException;

public class AnswerFullResponseSerializer extends JsonSerializer<AnswerFullResponse> { // TODO REFACTOR

    @Override
    public void serialize(AnswerFullResponse answerFullResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        writeAnswerData(answerFullResponse, jsonGenerator);
        AuthorSerializationUtil.writeAuthorUsername(answerFullResponse.getAuthor(), jsonGenerator);
        CommentAnswerSerializationUtil.writeCommentAnswers(answerFullResponse.getComments(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }

    private void writeAnswerData(AnswerFullResponse answerFullResponse, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeNumberField("id", answerFullResponse.getAnswerId());
        jsonGenerator.writeStringField("text", answerFullResponse.getText());
        jsonGenerator.writeStringField("answered", answerFullResponse.getAnswered() ? "true" : "false");
        jsonGenerator.writeStringField("creation_date", DateSerializationUtil.dateToString(answerFullResponse.getCreationDate()));
        jsonGenerator.writeNumberField("likes", answerFullResponse.getLikes());
        jsonGenerator.writeStringField("liked", answerFullResponse.isLiked() ? "true" : "false");
    }
}
