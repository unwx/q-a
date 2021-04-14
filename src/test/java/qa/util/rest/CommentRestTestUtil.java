package qa.util.rest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import qa.util.dao.query.params.CommentQueryParameters;

import java.math.BigInteger;

public class CommentRestTestUtil extends RestTestUtil {

    private CommentRestTestUtil() {
    }

    public static JSONObject commentEditJson() {
        JSONObject json = new JSONObject();
        json.put("id", 1L);
        json.put("text", CommentQueryParameters.SECOND_TEXT);
        return json;
    }

    public static JSONObject commentAnswerCreateJson() {
        JSONObject json = new JSONObject();
        json.put("answer_id", 1L);
        json.put("text", CommentQueryParameters.TEXT);
        return json;
    }

    public static JSONObject commentQuestionCreateJson() {
        JSONObject json = new JSONObject();
        json.put("question_id", 1L);
        json.put("text", CommentQueryParameters.TEXT);
        return json;
    }

    public static JSONObject id() {
        JSONObject json = new JSONObject();
        json.put("id", 1L);
        return json;
    }

    public static JSONObject idPage() {
        JSONObject json = new JSONObject();
        json.put("id", 1L);
        json.put("page", 1);
        return json;
    }

    public static JSONObject commentBADEditJson() {
        JSONObject json = new JSONObject();
        json.put("id", -1L);
        json.put("text", "wut");
        return json;
    }


    public static JSONObject commentAnswerBADCreateJson() {
        JSONObject json = new JSONObject();
        json.put("answer_id", -1L);
        json.put("text", "wut");
        return json;
    }

    public static JSONObject commentQuestionBADCreateJson() {
        JSONObject json = new JSONObject();
        json.put("question_id", -1L);
        json.put("text", "wut");
        return json;
    }

    public static JSONObject badId() {
        JSONObject json = new JSONObject();
        json.put("id", -1L);
        return json;
    }

    public static JSONObject badIdPage() {
        JSONObject json = new JSONObject();
        json.put("id", -1L);
        json.put("page", 0);
        return json;
    }

    @Nullable
    public static Long getId(String text, SessionFactory sessionFactory) {
        String sql = "SELECT id FROM comment WHERE text = :text";
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            BigInteger result = (BigInteger) session.createSQLQuery(sql).setParameter("text", text).uniqueResult();
            transaction.commit();
            return result == null ? null : result.longValue();
        }
    }


}
