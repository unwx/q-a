package qa.util.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import qa.domain.Answer;

import java.io.IOException;
import java.util.List;

public class AnswerSerializationUtil {

    private AnswerSerializationUtil() {
    }

    public static void writeAnswersWithComments(List<Answer> answers, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeArrayFieldStart("answers");
        for (Answer a : answers) {
            jsonGenerator.writeStartObject();
            writeAnswerDataComponents(a, jsonGenerator);
            CommentAnswerSerializationUtil.writeCommentAnswers(a.getComments(), jsonGenerator);
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }

    private static void writeAnswerDataComponents(Answer answer, JsonGenerator jsonGenerator) throws IOException {
        writeAnswerData(answer, jsonGenerator);
        AuthorSerializationUtil.writeAuthorUsername(answer.getAuthor(), jsonGenerator);
    }

    private static void writeAnswerData(Answer answer, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeNumberField("id", answer.getId());
        jsonGenerator.writeStringField("text", answer.getText());
        jsonGenerator.writeStringField("answered", answer.getAnswered() ? "true" : "false");
        jsonGenerator.writeStringField("creation_date", DateSerializationUtil.dateToString(answer.getCreationDate()));
    }
}
