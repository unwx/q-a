package qa.cache.entity.like.set;

import qa.cache.operation.KeyValueOperation;
import qa.cache.RedisKeys;

import java.io.Serial;
import java.io.Serializable;

public class UserToQuestionLikeSet implements Serializable, KeyValueOperation {

    @Serial
    private static final long serialVersionUID = -1204088405633094991L;

    private final String userId;
    private final String questionId;

    public UserToQuestionLikeSet(Long userId, Long questionId) {
        this.userId = String.valueOf(userId);
        this.questionId = String.valueOf(questionId);
    }

    @Override
    public String getKey() {
        return RedisKeys.getUserQuestionLikes(userId);
    }

    @Override
    public String getValue() {
        return questionId;
    }
}
