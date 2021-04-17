package qa.cache;

public class RedisKeys {
    public static final String USER_QUESTION_LIKES = "usr-que-l:";
    public static final String QUESTION_LIKES = "que-l:";

    public static String getUserQuestionLikes(String key) {
        return USER_QUESTION_LIKES + key;
    }

    public static String getQuestionLikes(String key) {
        return QUESTION_LIKES + key;
    }
}
