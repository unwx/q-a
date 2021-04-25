package qa.cache.entity.like.set;

import qa.cache.RedisKeys;
import qa.cache.operation.KeyValueOperation;

import java.io.Serial;
import java.io.Serializable;

public class QuestionToUserLikeSet implements Serializable, KeyValueOperation {

    @Serial
    private static final long serialVersionUID = -9157628175235393072L;

    private final String questionId;
    private final String userId;

    public QuestionToUserLikeSet(String questionId, String userId) {
        this.userId = userId;
        this.questionId = questionId;
    }

    @Override
    public String getKey() {
        return RedisKeys.getQuestionToUserLikes(questionId);
    }

    @Override
    public String getValue() {
        return userId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getUserId() {
        return userId;
    }
}
