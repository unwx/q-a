package qa.cache.like.entity;

import qa.cache.RedisKeys;
import qa.cache.abstraction.KeyOperation;

import java.io.Serial;
import java.io.Serializable;

public class CommentQuestionToLikeSet implements Serializable, KeyOperation {

    @Serial
    private static final long serialVersionUID = 2256178938541397880L;

    private final String commentId;

    public CommentQuestionToLikeSet(String commentId) {
        this.commentId = commentId;
    }

    @Override
    public String getKey() {
        return RedisKeys.getCommentQuestionLikes(commentId);
    }
}
