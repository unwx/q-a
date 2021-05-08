package util.rest;

import io.restassured.specification.RequestSpecification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import util.dao.query.params.CommentQueryParameters;

import java.math.BigInteger;

public class CommentRestTestUtil extends RestTestUtil {

    private static final String ID              = "id";
    private static final String PAGE            = "page";
    private static final String TEXT            = "text";
    private static final String ANSWER_ID       = "answer_id";
    private static final String QUESTION_OD     = "question_id";

    private CommentRestTestUtil() {}

    @Nullable
    public static Long getId(String text, SessionFactory sessionFactory) {
        final String sql = "SELECT id FROM comment WHERE text = :text";
        final BigInteger result;

        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            result = (BigInteger) session
                    .createSQLQuery(sql)
                    .setParameter("text", text)
                    .uniqueResult();

            transaction.commit();
        }
        return result == null ? null : result.longValue();
    }

    public static RequestSpecification commentAnswerCreateRequest(String accessToken) {
        final JSONObject json = commentAnswerCreateJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification commentAnswerCreateRequest(long answerId, String accessToken) {
        final JSONObject json = commentAnswerCreateJson(answerId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification commentAnswerCreateBadRequest(String accessToken) {
        final JSONObject json = badCommentAnswerCreateJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification commentQuestionCreateBadRequest(String accessToken) {
        final JSONObject json = badCommentQuestionCreateJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification commentQuestionCreateRequest(String accessToken) {
        final JSONObject json = commentQuestionCreateJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification commentQuestionCreateRequest(long questionId, String accessToken) {
        final JSONObject json = commentQuestionCreateJson(questionId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editCommentRequest(String accessToken) {
        final JSONObject json = commentEditJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editCommentRequest(long commentId, String accessToken) {
        final JSONObject json = commentEditJson(commentId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification editCommentBadRequest(String accessToken) {
        final JSONObject json = badCommentEditJson();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification idRequest(String accessToken) {
        final JSONObject json = id();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification idRequest(long commentId, String accessToken) {
        final JSONObject json = id(commentId);
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification idBadRequest(String accessToken) {
        final JSONObject json = badId();
        return getRequestJsonJwt(json.toString(), accessToken);
    }

    public static RequestSpecification idPageRequest() {
        final JSONObject json = idPage();
        return getRequestJson(json.toString());
    }

    public static RequestSpecification idPageBadRequest() {
        final JSONObject json = badIdPage();
        return getRequestJson(json.toString());
    }

    private static JSONObject commentEditJson() {
        final JSONObject json = new JSONObject();
        json.put(ID, 1L);
        json.put(TEXT, CommentQueryParameters.SECOND_TEXT);
        return json;
    }

    private static JSONObject commentEditJson(long commentId) {
        final JSONObject json = new JSONObject();
        json.put(ID, commentId);
        json.put(TEXT, CommentQueryParameters.SECOND_TEXT);
        return json;
    }

    private static JSONObject commentAnswerCreateJson() {
        final JSONObject json = new JSONObject();
        json.put(ANSWER_ID, 1L);
        json.put(TEXT, CommentQueryParameters.TEXT);
        return json;
    }

    private static JSONObject commentAnswerCreateJson(long answerId) {
        final JSONObject json = new JSONObject();
        json.put(ANSWER_ID, answerId);
        json.put(TEXT, CommentQueryParameters.TEXT);
        return json;
    }

    private static JSONObject commentQuestionCreateJson() {
        final JSONObject json = new JSONObject();
        json.put(QUESTION_OD, 1L);
        json.put(TEXT, CommentQueryParameters.TEXT);
        return json;
    }

    private static JSONObject commentQuestionCreateJson(long questionId) {
        final JSONObject json = new JSONObject();
        json.put(QUESTION_OD, questionId);
        json.put(TEXT, CommentQueryParameters.TEXT);
        return json;
    }

    private static JSONObject id() {
        final JSONObject json = new JSONObject();
        json.put(ID, 1L);
        return json;
    }

    private static JSONObject id(long commentId) {
        final JSONObject json = new JSONObject();
        json.put(ID, commentId);
        return json;
    }

    private static JSONObject idPage() {
        final JSONObject json = new JSONObject();
        json.put(ID, 1L);
        json.put(PAGE, 1);
        return json;
    }

    private static JSONObject badCommentEditJson() {
        final JSONObject json = new JSONObject();
        json.put(ID, -1L);
        json.put(TEXT, "wut");
        return json;
    }

    private static JSONObject badCommentAnswerCreateJson() {
        final JSONObject json = new JSONObject();
        json.put(ANSWER_ID, -1L);
        json.put(TEXT, "wut");
        return json;
    }

    private static JSONObject badCommentQuestionCreateJson() {
        final JSONObject json = new JSONObject();
        json.put(QUESTION_OD, -1L);
        json.put(TEXT, "wut");
        return json;
    }

    private static JSONObject badId() {
        final JSONObject json = new JSONObject();
        json.put(ID, -1L);
        return json;
    }

    private static JSONObject badIdPage() {
        final JSONObject json = new JSONObject();
        json.put(ID, -1L);
        json.put(PAGE, 0);
        return json;
    }
}
