package qa.ui;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import qa.util.rest.*;

public class UIRequests extends RestTestUtil {

    private UIRequests() {}

    public static RequestSpecification authorize() {
        final JSONObject json = new JSONObject();
        json.put("email", JwtTestUtil.USER_EMAIL);
        json.put("password", JwtTestUtil.USER_PASSWORD);

        final RequestSpecification request = RestAssured.given();
        request.body(json.toString());
        request.header("Content-Type", "application/json");
        return request;
    }

    public static RequestSpecification questionLike(long questionId, String accessToken) {
        final JSONObject json = new JSONObject();
        json.put("id", questionId);

        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification answerLike(long answerId, String accessToken) {
        final JSONObject json = new JSONObject();
        json.put("id", answerId);

        return getRequestJsonJwt(json.toString(), accessToken);
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
        final JSONObject json = AnswerRestTestUtil.createAnswerJson(questionId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editAnswer(long answerId, String accessToken) {
        final JSONObject json = AnswerRestTestUtil.editAnswerJson(answerId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification setAnswered(long answerId, String accessToken) {
        final JSONObject json = AnswerRestTestUtil.id(answerId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification deleteAnswer(long answerId, String accessToken) {
        final JSONObject json = AnswerRestTestUtil.id(answerId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification createCommentQuestion(long questionId, String accessToken) {
        final JSONObject json = CommentRestTestUtil.commentQuestionCreateJson(questionId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editCommentQuestion(long commentId, String accessToken) {
        final JSONObject json = CommentRestTestUtil.commentEditJson(commentId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification deleteCommentQuestion(long commentId, String accessToken) {
        final JSONObject json = CommentRestTestUtil.id(commentId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification createCommentAnswer(long answerId, String accessToken) {
        final JSONObject json = CommentRestTestUtil.commentAnswerCreateJson(answerId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editCommentAnswer(long commentId, String accessToken) {
        final JSONObject json = CommentRestTestUtil.commentEditJson(commentId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification deleteCommentAnswer(long commentId, String accessToken) {
        final JSONObject json = CommentRestTestUtil.id(commentId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }
}
