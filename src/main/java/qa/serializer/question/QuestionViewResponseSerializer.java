package qa.serializer.question;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.domain.User;
import qa.dto.response.question.QuestionViewResponse;
import qa.util.SerializerDateUtil;

import java.io.IOException;

public class QuestionViewResponseSerializer extends JsonSerializer<QuestionViewResponse> {

    @Override
    public void serialize(QuestionViewResponse questionViewResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        writeViewData(questionViewResponse, jsonGenerator);
        writeViewTags(questionViewResponse.getTags(), jsonGenerator);
        writeViewAuthor(questionViewResponse.getUser(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }

    private void writeViewData(QuestionViewResponse response, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeNumberField("id", response.getQuestionId());
        jsonGenerator.writeNumberField("answers_count", response.getAnswersCount());
        jsonGenerator.writeStringField("title", response.getTitle());
        jsonGenerator.writeStringField(
                "creation_date",
                SerializerDateUtil.dateToString(response.getCreationDate(), SerializerDateUtil.DEFAULT_DATE_FORMAT));
        jsonGenerator.writeStringField(
                "last_activity",
                SerializerDateUtil.dateToString(response.getCreationDate(), SerializerDateUtil.DEFAULT_DATE_FORMAT));
    }

    private void writeViewTags(String[] tags, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeArrayFieldStart("tags");
        for (String s : tags) {
            jsonGenerator.writeString(s);
        }
        jsonGenerator.writeEndArray();
    }

    private void writeViewAuthor(User author, JsonGenerator jsonGenerator)throws IOException {
        jsonGenerator.writeObjectFieldStart("author");
        jsonGenerator.writeStringField("username", author.getUsername());
        jsonGenerator.writeEndObject();
    }
}
