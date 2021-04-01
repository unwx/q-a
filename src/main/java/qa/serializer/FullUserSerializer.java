package qa.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import qa.domain.Answer;
import qa.domain.Question;
import qa.dto.response.user.UserFullResponse;

import java.io.IOException;
import java.util.List;

public class FullUserSerializer extends JsonSerializer<UserFullResponse> {

    @Override
    public void serialize(@NotNull UserFullResponse userFullResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        writeUserData(userFullResponse, jsonGenerator);
        writeQuestionsData(userFullResponse.getQuestions(), jsonGenerator);
        writeAnswersData(userFullResponse.getAnswers(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }

    private void writeUserData(UserFullResponse userFullResponse, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeNumberField("id", userFullResponse.getUserId());
        jsonGenerator.writeStringField("username", userFullResponse.getUsername());
        jsonGenerator.writeStringField("about", userFullResponse.getAbout());
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
