package qa.serializer.user;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jetbrains.annotations.NotNull;
import qa.domain.Answer;
import qa.domain.Question;
import qa.dto.response.user.UserFullResponse;

import java.io.IOException;
import java.util.List;

public class FullUserResponseSerializer extends JsonSerializer<UserFullResponse> {

    private static final String ID              = "id";
    private static final String USERNAME        = "username";
    private static final String ABOUT           = "about";

    private static final String QUESTION        = "questions";
    private static final String QUESTION_ID     = "id";
    private static final String QUESTION_TITLE  = "title";

    private static final String ANSWER          = "answers";
    private static final String ANSWER_ID       = "id";
    private static final String ANSWER_TEXT     = "text";

    @Override
    public void serialize(@NotNull UserFullResponse userFullResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        writeUserData(userFullResponse, jsonGenerator);
        writeQuestionsData(userFullResponse.getQuestions(), jsonGenerator);
        writeAnswersData(userFullResponse.getAnswers(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }

    private void writeUserData(UserFullResponse userFullResponse, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeNumberField(ID, userFullResponse.getUserId());
        jsonGenerator.writeStringField(USERNAME, userFullResponse.getUsername());
        jsonGenerator.writeStringField(ABOUT, userFullResponse.getAbout());
    }

    private void writeQuestionsData(List<Question> questions, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeArrayFieldStart(QUESTION);
        for (Question q : questions) {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField(QUESTION_ID, q.getId());
            jsonGenerator.writeStringField(QUESTION_TITLE, q.getTitle());

            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }

    private void writeAnswersData(List<Answer> answers, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeArrayFieldStart(ANSWER);
        for (Answer a : answers) {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField(ANSWER_ID, a.getId());
            jsonGenerator.writeStringField(ANSWER_TEXT, a.getText());

            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }
}
