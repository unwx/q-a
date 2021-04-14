package qa.util.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import qa.domain.CommentQuestion;
import qa.dto.response.comment.CommentAnswerResponse;
import qa.dto.response.comment.CommentQuestionResponse;

import java.io.IOException;
import java.util.List;

public class CommentQuestionSerializationUtil {

    private CommentQuestionSerializationUtil() {
    }

    public static void writeCommentQuestions(List<CommentQuestion> commentQuestions, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeArrayFieldStart("comments");
        for (CommentQuestion c : commentQuestions) {
            jsonGenerator.writeStartObject();
            writeCommentQuestionDataComponents(c, jsonGenerator);
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }

    public static void writeCommentQuestion(CommentQuestionResponse response, JsonGenerator jsonGenerator) throws IOException {
        writeCommentQuestionDataComponents(new CommentQuestion(
                response.getCommentId(),
                response.getText(),
                response.getCreationDate(),
                response.getAuthor()
        ), jsonGenerator);
    }

    public static void writeCommentAnswer(CommentAnswerResponse response, JsonGenerator jsonGenerator) throws IOException {
        writeCommentQuestionDataComponents(new CommentQuestion(
                response.getCommentId(),
                response.getText(),
                response.getCreationDate(),
                response.getAuthor()
        ), jsonGenerator);
    }

    private static void writeCommentQuestionDataComponents(CommentQuestion c, JsonGenerator jsonGenerator) throws IOException {
        writeCommentQuestionData(c, jsonGenerator);
        AuthorSerializationUtil.writeAuthorUsername(c.getAuthor(), jsonGenerator);
    }

    private static void writeCommentQuestionData(CommentQuestion c, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeNumberField("id", c.getId());
        jsonGenerator.writeStringField("text", c.getText());
        jsonGenerator.writeStringField("creation_date", DateSerializationUtil.dateToString(c.getCreationDate()));
    }
}
