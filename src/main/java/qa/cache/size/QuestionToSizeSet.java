package qa.cache.size;

import qa.cache.KeyOperation;
import qa.cache.RedisKeys;

import java.io.Serial;
import java.io.Serializable;

public class QuestionToSizeSet implements Serializable, KeyOperation {

    @Serial
    private static final long serialVersionUID = -7576190205071548700L;

    private final String questionId;

    public QuestionToSizeSet(Long questionId) {
        this.questionId = String.valueOf(questionId);
    }

    public QuestionToSizeSet(String questionId) {
        this.questionId = questionId;
    }

    @Override
    public String getKey() {
        return RedisKeys.getUserQuestionLikes(questionId);
    }

    @Override
    public String getClearKey() {
        return questionId;
    }
}
