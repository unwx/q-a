package qa.cache.entity.like.set;

import qa.cache.operation.KeyOperation;
import qa.cache.RedisKeys;

import java.io.Serial;
import java.io.Serializable;

public class QuestionToLikeSet implements Serializable, KeyOperation {

    @Serial
    private static final long serialVersionUID = -7576190205071548700L;

    private final String questionId;

    public QuestionToLikeSet(Long questionId) {
        this.questionId = String.valueOf(questionId);
    }

    public QuestionToLikeSet(String questionId) {
        this.questionId = questionId;
    }

    @Override
    public String getKey() {
        return RedisKeys.getQuestionLikes(questionId);
    }
}
