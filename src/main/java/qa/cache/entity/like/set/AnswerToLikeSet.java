package qa.cache.entity.like.set;

import qa.cache.operation.KeyOperation;
import qa.cache.RedisKeys;

import java.io.Serial;
import java.io.Serializable;

public class AnswerToLikeSet implements Serializable, KeyOperation {

    @Serial
    private static final long serialVersionUID = 9117150078664520792L;

    private final String answerId;

    public AnswerToLikeSet(long answerId) {
        this.answerId = String.valueOf(answerId);
    }

    @Override
    public String getKey() {
        return RedisKeys.getAnswerLikes(answerId);
    }
}