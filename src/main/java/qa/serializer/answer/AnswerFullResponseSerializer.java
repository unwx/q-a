package qa.serializer.answer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.domain.CommentAnswer;
import qa.domain.User;
import qa.dto.response.answer.AnswerFullResponse;
import qa.util.serialization.DateSerializationUtil;

import java.io.IOException;
import java.util.List;

public class AnswerFullResponseSerializer extends JsonSerializer<AnswerFullResponse> {

    private static final AnswerSerializer answerSerializer = new AnswerSerializer();

    @Override
    public void serialize(AnswerFullResponse response, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        final long id = response.getAnswerId();
        final int likes = response.getLikes();
        final boolean liked = response.isLiked();
        final boolean answered = response.getAnswered();
        final String text = response.getText();
        final String creationDate = DateSerializationUtil.dateToString(response.getCreationDate());

        final User author = response.getAuthor();
        final List<CommentAnswer> comments = response.getComments();
        answerSerializer.serialize(id, likes, liked, answered, text, creationDate, author, comments, gen, serializerProvider);
    }
}
