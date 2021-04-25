package qa.cache.entity.like.set;

import qa.cache.RedisKeys;
import qa.cache.operation.KeyValueOperation;

import java.io.Serial;
import java.io.Serializable;

public class AnswerToUserLikeSet implements Serializable, KeyValueOperation {

    @Serial
    private static final long serialVersionUID = -8188437480772570134L;

    private final String answerId;
    private final String userId;

    public AnswerToUserLikeSet(long answerId, long userId) {
        this.answerId = String.valueOf(answerId);
        this.userId = String.valueOf(userId);
    }

    @Override
    public String getKey() {
        return RedisKeys.getAnswerToUserLikes(answerId);
    }

    @Override
    public String getValue() {
        return userId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public String getUserId() {
        return userId;
    }
}