package qa.util.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import qa.domain.CommentAnswer;

import java.io.IOException;
import java.util.List;

public class CommentAnswerSerializationUtil {

    private CommentAnswerSerializationUtil() {
    }

    public static void writeCommentAnswers(List<CommentAnswer> comments, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeArrayFieldStart("comments");
        for (CommentAnswer c : comments) {
            jsonGenerator.writeStartObject();
            writeCommentAnswerDataComponents(c, jsonGenerator);
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }

    private static void writeCommentAnswerDataComponents(CommentAnswer comment, JsonGenerator jsonGenerator) throws IOException {
        writeCommentAnswerData(comment, jsonGenerator);
        AuthorSerializationUtil.writeAuthorUsername(comment.getAuthor(), jsonGenerator);
    }

    private static void writeCommentAnswerData(CommentAnswer comment, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeNumberField("id", comment.getId());
        jsonGenerator.writeStringField("text", comment.getText());
        jsonGenerator.writeStringField("creation_date", DateSerializationUtil.dateToString(comment.getCreationDate()));
    }
}
