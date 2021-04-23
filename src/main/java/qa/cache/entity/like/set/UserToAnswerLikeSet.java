package qa.cache.entity.like.set;

import qa.cache.RedisKeys;
import qa.cache.operation.KeyValueOperation;

import java.io.Serial;
import java.io.Serializable;

public class UserToAnswerLikeSet implements Serializable, KeyValueOperation {

    @Serial
    private static final long serialVersionUID = 1293650366015411185L;

    private final String userId;
    private final String answerId;

    public UserToAnswerLikeSet(long userId, long answerId) {
        this.userId = String.valueOf(userId);
        this.answerId = String.valueOf(answerId);
    }

    @Override
    public String getKey() {
        return RedisKeys.getUserToAnswerLikes(userId);
    }

    @Override
    public String getValue() {
        return answerId;
    }
}
