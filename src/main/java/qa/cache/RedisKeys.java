package qa.cache;

public class RedisKeys {

    public static final String USER_QUESTION_LIKES              = "usr-que-l:";
    public static final String USER_ANSWER_LIKES                = "usr-ans-l:";
    public static final String USER_COMMENT_QUESTION_LIKES      = "usr-com-que-l:";
    public static final String USER_COMMENT_ANSWER_LIKES        = "usr-com-ans-l:";

    public static final String QUESTION_USER_LIKES              = "que-usr-l:";
    public static final String ANSWER_USER_LIKES                = "ans-usr-l:";
    public static final String COMMENT_QUESTION_USER_LIKES      = "com-que-usr-l:";
    public static final String COMMENT_ANSWER_USER_LIKES        = "com-ans-usr-l:";

    public static final String QUESTION_LIKES                   = "que-l:";
    public static final String ANSWER_LIKES                     = "ans-l:";
    public static final String COMMENT_QUESTION_LIKES           = "com-que-l:";
    public static final String COMMENT_ANSWER_LIKES             = "com-ans-l:";

    public static String getUserToQuestionLikes(String key) {
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

    public static String getQuestionToUserLikes(String key) {
        return QUESTION_USER_LIKES + key;
    }

    public static String getAnswerToUserLikes(String key) {
        return ANSWER_USER_LIKES + key;
    }

    public static String getCommentQuestionToUserLikes(String key) {
        return COMMENT_QUESTION_USER_LIKES + key;
    }

    public static String getCommentAnswerToUserLikes(String key) {
        return COMMENT_ANSWER_USER_LIKES + key;
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
