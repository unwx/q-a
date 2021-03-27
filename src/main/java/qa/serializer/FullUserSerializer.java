package qa.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import qa.domain.Answer;
import qa.domain.Question;
import qa.dto.response.user.FullUserResponse;

import java.io.IOException;
import java.util.List;

public class FullUserSerializer extends JsonSerializer<FullUserResponse> {

    @Override
    public void serialize(@NotNull FullUserResponse fullUserResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        writeUserData(fullUserResponse, jsonGenerator);
        writeQuestionsData(fullUserResponse.getQuestions(), jsonGenerator);
        writeAnswersData(fullUserResponse.getAnswers(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }

    private void writeUserData(FullUserResponse fullUserResponse, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStringField("username", fullUserResponse.getUsername());
        jsonGenerator.writeStringField("about", fullUserResponse.getAbout());
    }

    private void writeQuestionsData(@Nullable List<Question> questions, JsonGenerator jsonGenerator) throws IOException {
        if (questions == null)
            return;

        jsonGenerator.writeArrayFieldStart("questions");
        for (Question q : questions) {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("id", q.getId());
            jsonGenerator.writeStringField("title", q.getTitle());

            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }

    private void writeAnswersData(@Nullable List<Answer> answers, JsonGenerator jsonGenerator) throws IOException {
        if (answers == null)
            return;

        jsonGenerator.writeArrayFieldStart("answers");
        for (Answer a : answers) {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("id", a.getId());
            jsonGenerator.writeStringField("text", a.getText());

            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }
}
