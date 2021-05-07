package qa.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.specification.RequestSpecification;
import qa.dto.response.question.QuestionFullResponse;
import qa.dto.response.question.QuestionViewResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UIRequestExecutor {

    public static final String AUTHENTICATION               = "authentication";
    public static final String QUESTION                     = "question";
    public static final String ANSWER                       = "answer";
    public static final String COMMENT_QUESTION             = "comment/question";
    public static final String COMMENT_ANSWER               = "comment/answer";

    public static final String LOGIN                        = AUTHENTICATION    + "/login";

    public static final String QUESTION_CREATE              = QUESTION          + "/create";
    public static final String QUESTION_EDIT                = QUESTION          + "/edit";
    public static final String QUESTION_DELETE              = QUESTION          + "/delete";
    public static final String QUESTION_VIEW                = QUESTION          + "/get/views/%s";
    public static final String QUESTION_FULL                = QUESTION          + "/get/full/%s";
    public static final String QUESTION_LIKE                = QUESTION          + "/like";

    public static final String ANSWER_CREATE                = ANSWER            + "/create";
    public static final String ANSWER_EDIT                  = ANSWER            + "/edit";
    public static final String ANSWER_ANSWERED              = ANSWER            + "/answered";
    public static final String ANSWER_NOT_ANSWERED          = ANSWER            + "/not-answered";
    public static final String ANSWER_DELETE                = ANSWER            + "/delete";
    public static final String ANSWER_LIKE                  = ANSWER            + "/like";

    public static final String COMMENT_QUESTION_CREATE      = COMMENT_QUESTION  + "/create";
    public static final String COMMENT_QUESTION_EDIT        = COMMENT_QUESTION  + "/edit";
    public static final String COMMENT_QUESTION_DELETE      = COMMENT_QUESTION  + "/delete";

    public static final String COMMENT_ANSWER_CREATE        = COMMENT_ANSWER    + "/create";
    public static final String COMMENT_ANSWER_EDIT          = COMMENT_ANSWER    + "/edit";
    public static final String COMMENT_ANSWER_DELETE        = COMMENT_ANSWER    + "/delete";

    private static final ObjectMapper mapper = UIRequests.getObjectMapper();

    public static QuestionViewResponse[] getViews(RequestSpecification request) throws JsonProcessingException {
        final String path = QUESTION_VIEW.formatted(1);
        final String viewsBody = request
                .get(path)
                .body()
                .asString();

        return mapper.readValue(viewsBody, QuestionViewResponse[].class);
    }

    public static QuestionFullResponse getQuestion(RequestSpecification request, long questionId) throws JsonProcessingException {
        final String path = QUESTION_FULL.formatted(questionId);
        final String questionBody = request
                .get(path)
                .body()
                .asString();

        return mapper.readValue(questionBody, QuestionFullResponse.class);
    }

    public static QuestionFullResponse getQuestion(RequestSpecification request) throws JsonProcessingException {
        final QuestionViewResponse[] views = getViews(request);
        final int viewTarget = (int) (Math.random() * views.length);
        final long questionId = views[viewTarget].getQuestionId();
        return getQuestion(request, questionId);
    }

    public static long createQuestion(RequestSpecification request) {
        final String responseId = request.post(QUESTION_CREATE).body().asString();
        return Long.parseLong(responseId);
    }

    public static void editQuestion(RequestSpecification request) {
        final int status = request.put(QUESTION_EDIT).statusCode();
        assertThat(status, equalTo(200));
    }

    public static void deleteQuestion(RequestSpecification request) {
        final int status = request.delete(QUESTION_DELETE).getStatusCode();
        assertThat(status, equalTo(200));
    }

    public static void likeQuestion(RequestSpecification request) {
        final int status = request.post(QUESTION_LIKE).getStatusCode();
        assertThat(status, equalTo(200));
    }

    public static long createAnswer(RequestSpecification request) {
        final String responseId = request.post(ANSWER_CREATE).body().asString();
        return Long.parseLong(responseId);
    }

    public static void editAnswer(RequestSpecification request) {
        final int status = request.put(ANSWER_EDIT).statusCode();
        assertThat(status, equalTo(200));
    }

    public static void setAnswered(RequestSpecification request) {
        final int status = request.post(ANSWER_ANSWERED).getStatusCode();
        assertThat(status, equalTo(200));
    }

    public static void removeAnswered(RequestSpecification request) {
        final int status = request.post(ANSWER_NOT_ANSWERED).getStatusCode();
        assertThat(status, equalTo(200));
    }

    public static void likeAnswer(RequestSpecification request) {
        final int status = request.post(ANSWER_LIKE).getStatusCode();
        assertThat(status, equalTo(200));
    }

    public static void deleteAnswer(RequestSpecification request) {
        final int status = request.delete(ANSWER_DELETE).statusCode();
        assertThat(status, equalTo(200));
    }

    public static long createCommentQuestion(RequestSpecification request) {
        final String responseId = request.post(COMMENT_QUESTION_CREATE).body().asString();
        return Long.parseLong(responseId);
    }

    public static void editCommentQuestion(RequestSpecification request) {
        final int status = request.put(COMMENT_QUESTION_EDIT).getStatusCode();
        assertThat(status, equalTo(200));
    }

    public static void deleteCommentQuestion(RequestSpecification request) {
        final int status = request.delete(COMMENT_QUESTION_DELETE).getStatusCode();
        assertThat(status, equalTo(200));
    }

    public static long createCommentAnswer(RequestSpecification request) {
        final String responseId = request.post(COMMENT_ANSWER_CREATE).body().asString();
        return Long.parseLong(responseId);
    }

    public static void editCommentAnswer(RequestSpecification request) {
        final int status = request.put(COMMENT_ANSWER_EDIT).getStatusCode();
        assertThat(status, equalTo(200));
    }

    public static void deleteCommentAnswer(RequestSpecification request) {
        final int status = request.delete(COMMENT_ANSWER_DELETE).getStatusCode();
        assertThat(status, equalTo(200));
    }
}
