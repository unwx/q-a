package qa.serializer.question;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.dto.response.question.QuestionViewResponse;
import qa.util.serialization.AuthorSerializationUtil;
import qa.util.serialization.DateSerializationUtil;
import qa.util.serialization.QuestionSerializationUtil;

import java.io.IOException;

public class QuestionViewResponseSerializer extends JsonSerializer<QuestionViewResponse> {

    @Override
    public void serialize(QuestionViewResponse questionViewResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        writeViewData(questionViewResponse, jsonGenerator);
        QuestionSerializationUtil.writeViewTags(questionViewResponse.getTags(), jsonGenerator);
        AuthorSerializationUtil.writeAuthorUsername(questionViewResponse.getUser(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }

    private void writeViewData(QuestionViewResponse response, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeNumberField("id", response.getQuestionId());
        jsonGenerator.writeNumberField("answers_count", response.getAnswersCount());
        jsonGenerator.writeStringField("title", response.getTitle());
        jsonGenerator.writeStringField("creation_date", DateSerializationUtil.dateToString(response.getCreationDate()));
        jsonGenerator.writeStringField("last_activity", DateSerializationUtil.dateToString(response.getCreationDate()));
    }
}
