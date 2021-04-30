package qa.cache.like.entity;

import qa.cache.RedisKeys;
import qa.cache.abstraction.KeyValueOperation;

import java.io.Serial;
import java.io.Serializable;

public class UserToCommentQuestionLikeSet implements Serializable, KeyValueOperation {

    @Serial
    private static final long serialVersionUID = -4273721487703922230L;

    private final String userId;
    private final String commentId;

    public UserToCommentQuestionLikeSet(String userId, String commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }

    @Override
    public String getKey() {
        return RedisKeys.getUserToCommentQuestionLikes(userId);
    }

    @Override
    public String getValue() {
        return commentId;
    }
}
