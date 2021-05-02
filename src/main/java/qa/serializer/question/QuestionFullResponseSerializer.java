package qa.serializer.question;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import qa.domain.Answer;
import qa.domain.CommentQuestion;
import qa.domain.User;
import qa.dto.response.question.QuestionFullResponse;
import qa.serializer.answer.AnswerSerializer;
import qa.serializer.comment.CommentSerializer;
import qa.serializer.user.AuthorSerializer;
import qa.serializer.util.DateSerializationUtil;

import java.io.IOException;
import java.util.List;

public class QuestionFullResponseSerializer extends JsonSerializer<QuestionFullResponse> {

    private static final String ID = "id";
    private static final String TEXT = "text";
    private static final String TITLE = "title";
    private static final String CREATION_DATE = "creation_date";
    private static final String LAST_ACTIVITY = "last_activity";
    private static final String LIKES = "likes";
    private static final String LIKED = "liked";

    private static final QuestionTagsSerializer questionTagsSerializer = new QuestionTagsSerializer();
    private static final AnswerSerializer answerSerializer = new AnswerSerializer();
    private static final CommentSerializer commentSerializer = new CommentSerializer();
    private static final AuthorSerializer authorSerializer = new AuthorSerializer();

    @Override
    public void serialize(QuestionFullResponse response, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        final long id = response.getQuestionId();
        final int likes = response.getLikes();
        final boolean liked = response.isLiked();
        final String text = response.getText();
        final String title = response.getTitle();
        final String creation_date = DateSerializationUtil.dateToString(response.getCreationDate());
        final String last_activity = DateSerializationUtil.dateToString(response.getLastActivity());
        final String[] tags = response.getTags();

        final User author = response.getAuthor();
        final List<Answer> answers = response.getAnswers();
        final List<CommentQuestion> commentQuestions = response.getComments();

        gen.writeStartObject();
        gen.writeNumberField(ID, id);
        gen.writeStringField(TEXT, text);
        gen.writeStringField(TITLE, title);
        gen.writeStringField(CREATION_DATE, creation_date);
        gen.writeStringField(LAST_ACTIVITY, last_activity);
        gen.writeNumberField(LIKES, likes);
        gen.writeBooleanField(LIKED, liked);

        questionTagsSerializer.serialize(tags, gen, serializerProvider);
        authorSerializer.serialize(author, gen, serializerProvider);
        answerSerializer.serialize(answers, gen, serializerProvider);
        commentSerializer.serialize(commentQuestions, gen, serializerProvider);
        gen.writeEndObject();
    }
}
