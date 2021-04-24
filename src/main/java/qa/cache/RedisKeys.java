package qa.cache;

public class RedisKeys {
    private static final String USER_QUESTION_LIKES = "usr-que-l:";
    private static final String USER_ANSWER_LIKES = "usr-ans-l:";
    private static final String USER_COMMENT_QUESTION_LIKES = "usr-com-que-l:";
    private static final String USER_COMMENT_ANSWER_LIKES = "usr-com-ans-l:";

    private static final String QUESTION_LIKES = "que-l:";
    private static final String ANSWER_LIKES = "ans-l:";
    private static final String COMMENT_QUESTION_LIKES = "com-que-l:";
    private static final String COMMENT_ANSWER_LIKES = "com-ans-l:";

    public static String getUserQuestionLikes(String key) {
        return USER_QUESTION_LIKES + key;
    }

    public static String getUserToAnswerLikes(String key) {
        return USER_ANSWER_LIKES + key;
    }

    public static String getUserToCommentQuestionLikes(String key) {
        return USER_COMMENT_QUESTION_LIKES + key;
    }

    public static String getUserToCommentAnswerLikes(String key) {
        return USER_COMMENT_ANSWER_LIKES + key;
    }

    public static String getQuestionLikes(String key) {
        return QUESTION_LIKES + key;
    }

    public static String getAnswerLikes(String key) {
        return ANSWER_LIKES + key;
    }

    public static String getCommentQuestionLikes(String key) {
        return COMMENT_QUESTION_LIKES + key;
    }

    public static String getCommentAnswerLikes(String key) {
        return COMMENT_ANSWER_LIKES + key;
    }
}
