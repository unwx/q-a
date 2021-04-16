package qa.cache;

public class RedisKeys {
    public static final String USER_QUESTION_LIKES = "us-qu-l";
    public static final String QUESTION_SIZE = "qu-si";

    public static String getUserQuestionLikes(String key) {
        return USER_QUESTION_LIKES + key;
    }

    public static String getQuestionSize(String key) {
        return QUESTION_SIZE + key;
    }
}
