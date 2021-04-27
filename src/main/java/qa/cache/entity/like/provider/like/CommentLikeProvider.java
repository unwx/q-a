package qa.cache.entity.like.provider.like;

import qa.cache.operation.CommentToLikeSetOperation;
import qa.cache.operation.IUserCommentLikeSetOperation;
import redis.clients.jedis.Jedis;

public abstract class CommentLikeProvider extends LikesProvider {

    private final IUserCommentLikeSetOperation userCommentOperation;
    private final CommentToLikeSetOperation commentOperation;

    protected CommentLikeProvider(IUserCommentLikeSetOperation userCommentOperation,
                                  CommentToLikeSetOperation commentOperation) {
        this.userCommentOperation = userCommentOperation;
        this.commentOperation = commentOperation;
    }

    protected void initLike(long commentId,
                         Jedis jedis) {

        final String commentIdStr = String.valueOf(commentId);
        super.initLike(commentIdStr, commentOperation, jedis);
    }

    protected void like(long userId,
                     long commentId,
                     Jedis jedis) {

        final String userIdStr = String.valueOf(userId);
        final String commentIdStr = String.valueOf(commentId);

        super.like(userIdStr, commentIdStr, userCommentOperation, commentOperation, jedis);
    }
}
