package qa.cache.entity.like.set;

import qa.cache.RedisKeys;
import qa.cache.operation.KeyOperation;

import java.io.Serial;
import java.io.Serializable;

public class CommentAnswerToLikeSet implements Serializable, KeyOperation {

    @Serial
    private static final long serialVersionUID = 3926768006937712248L;

    private final String commentId;

    public CommentAnswerToLikeSet(String commentId) {
        this.commentId = commentId;
    }

    @Override
    public String getKey() {
        return RedisKeys.getCommentAnswerLikes(commentId);
    }
}