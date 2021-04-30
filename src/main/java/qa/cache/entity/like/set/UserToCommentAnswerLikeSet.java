package qa.cache.entity.like.set;

import qa.cache.RedisKeys;
import qa.cache.operation.KeyValueOperation;

import java.io.Serial;
import java.io.Serializable;

public class UserToCommentAnswerLikeSet implements Serializable, KeyValueOperation {

    @Serial
    private static final long serialVersionUID = -3636320433658729493L;

    private final String userId;
    private final String commentId;

    public UserToCommentAnswerLikeSet(String userId, String commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }

    @Override
    public String getKey() {
        return RedisKeys.getUserToCommentAnswerLikes(userId);
    }

    @Override
    public String getValue() {
        return commentId;
    }
}

