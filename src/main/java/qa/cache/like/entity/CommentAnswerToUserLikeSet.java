package qa.cache.like.entity;

import qa.cache.RedisKeys;
import qa.cache.abstraction.KeyValueOperation;

import java.io.Serial;
import java.io.Serializable;

public class CommentAnswerToUserLikeSet implements Serializable, KeyValueOperation {

    @Serial
    private static final long serialVersionUID = 4813002098370784056L;

    private final String commentId;
    private final String userId;

    public CommentAnswerToUserLikeSet(String commentId, String userId) {
        this.commentId = commentId;
        this.userId = userId;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String getKey() {
        return RedisKeys.getCommentAnswerToUserLikes(commentId);
    }

    @Override
    public String getValue() {
        return userId;
    }
}
