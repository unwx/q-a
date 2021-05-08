package qa.ui;

import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import util.rest.*;

public class UIRequests extends RestTestUtil {

    private UIRequests() {}

    public static RequestSpecification authorize() {
        return AuthenticationRestTestUtil.getLoginRequest();
    }

    public static RequestSpecification questionLike(long questionId, String accessToken) {
        final JSONObject json = QuestionRestTestUtil.id(questionId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification answerLike(long answerId, String accessToken) {
        return AnswerRestTestUtil.idRequest(answerId, accessToken);
    }

    public static RequestSpecification createQuestion(String accessToken) {
        final JSONObject json = QuestionRestTestUtil.createQuestionJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editQuestion(long questionId, String accessToken) {
        final JSONObject json = QuestionRestTestUtil.editQuestionJson(questionId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification deleteQuestion(long questionId, String accessToken) {
        final JSONObject json = QuestionRestTestUtil.id(questionId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification createAnswer(long questionId, String accessToken) {
        return AnswerRestTestUtil.createAnswerRequest(questionId, accessToken);
    }

    public static RequestSpecification editAnswer(long answerId, String accessToken) {
        return AnswerRestTestUtil.editAnswerRequest(answerId, accessToken);
    }

    public static RequestSpecification setAnswered(long answerId, String accessToken) {
        return AnswerRestTestUtil.idRequest(answerId, accessToken);
    }

    public static RequestSpecification deleteAnswer(long answerId, String accessToken) {
        return AnswerRestTestUtil.idRequest(answerId, accessToken);
    }

    public static RequestSpecification createCommentQuestion(long questionId, String accessToken) {
        return CommentRestTestUtil.commentQuestionCreateRequest(questionId, accessToken);
    }

    public static RequestSpecification editCommentQuestion(long commentId, String accessToken) {
        return CommentRestTestUtil.editCommentRequest(commentId, accessToken);
    }

    public static RequestSpecification deleteCommentQuestion(long commentId, String accessToken) {
        return CommentRestTestUtil.idRequest(commentId, accessToken);
    }

    public static RequestSpecification createCommentAnswer(long answerId, String accessToken) {
        return CommentRestTestUtil.commentAnswerCreateRequest(answerId, accessToken);
    }

    public static RequestSpecification editCommentAnswer(long commentId, String accessToken) {
        return CommentRestTestUtil.editCommentRequest(commentId, accessToken);
    }

    public static RequestSpecification deleteCommentAnswer(long commentId, String accessToken) {
        return CommentRestTestUtil.idRequest(commentId, accessToken);
    }
}
