package qa.cache.like.entity;

import qa.cache.RedisKeys;
import qa.cache.abstraction.KeyValueOperation;

import java.io.Serial;
import java.io.Serializable;

public class UserToQuestionLikeSet implements Serializable, KeyValueOperation {

    @Serial
    private static final long serialVersionUID = -1204088405633094991L;

    private final String userId;
    private final String questionId;

    public UserToQuestionLikeSet(String userId, String questionId) {
        this.userId = userId;
        this.questionId = questionId;
    }

    @Override
    public String getKey() {
        return RedisKeys.getUserToQuestionLikes(userId);
    }

    @Override
    public String getValue() {
        return questionId;
    }
}
