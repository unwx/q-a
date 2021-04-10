package qa.serializer.question;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.dto.response.question.QuestionFullResponse;
import qa.util.serialization.*;

import java.io.IOException;

public class QuestionFullResponseSerializer extends JsonSerializer<QuestionFullResponse> {

    @Override
    public void serialize(QuestionFullResponse questionFullResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        writeQuestionData(questionFullResponse, jsonGenerator);
        QuestionSerializationUtil.writeViewTags(questionFullResponse.getTags(), jsonGenerator);
        AuthorSerializationUtil.writeAuthorUsername(questionFullResponse.getAuthor(), jsonGenerator);
        CommentQuestionSerializationUtil.writeCommentQuestions(questionFullResponse.getComments(), jsonGenerator);
        AnswerSerializationUtil.writeAnswersWithComments(questionFullResponse.getAnswers(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }

    private void writeQuestionData(QuestionFullResponse response, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeNumberField("id", response.getQuestionId());
        jsonGenerator.writeStringField("text", response.getText());
        jsonGenerator.writeStringField("title", response.getTitle());
        jsonGenerator.writeStringField("creation_date", DateSerializationUtil.dateToString(response.getCreationDate()));
        jsonGenerator.writeStringField("last_activity", DateSerializationUtil.dateToString(response.getCreationDate()));
    }
}
