package qa.serializer.question;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.domain.User;
import qa.dto.response.question.QuestionViewResponse;
import qa.serializer.user.AuthorSerializer;
import qa.util.serialization.DateSerializationUtil;

import java.io.IOException;

public class QuestionViewResponseSerializer extends JsonSerializer<QuestionViewResponse> {

    private static final String ID = "id";
    private static final String ANSWERS_COUNT = "answers_count";
    private static final String TITLE = "title";
    private static final String CREATION_DATE = "creation_date";
    private static final String LAST_ACTIVITY = "last_activity";

    private static final QuestionTagsSerializer questionTagsSerializer = new QuestionTagsSerializer();
    private static final AuthorSerializer authorSerializer = new AuthorSerializer();

    @Override
    public void serialize(QuestionViewResponse response, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        final long id = response.getQuestionId();
        final int count = response.getAnswersCount();
        final String title = response.getTitle();
        final String creationDate = DateSerializationUtil.dateToString(response.getCreationDate());
        final String lastActivity = DateSerializationUtil.dateToString(response.getLastActivity());
        final String[] tags = response.getTags();
        final User author = response.getUser();

        gen.writeStartObject();
        gen.writeNumberField(ID, id);
        gen.writeNumberField(ANSWERS_COUNT, count);
        gen.writeStringField(TITLE, title);
        gen.writeStringField(CREATION_DATE, creationDate);
        gen.writeStringField(LAST_ACTIVITY, lastActivity);

        questionTagsSerializer.serialize(tags, gen, serializerProvider);
        authorSerializer.serialize(author, gen, serializerProvider);
        gen.writeEndObject();
    }
}
