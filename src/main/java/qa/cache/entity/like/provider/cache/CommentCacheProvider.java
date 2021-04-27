package qa.cache.entity.like.provider.cache;

import qa.cache.CacheProvider;
import qa.cache.operation.CommentToLikeSetOperation;
import qa.cache.operation.IUserCommentLikeSetOperation;
import qa.domain.Comment;
import redis.clients.jedis.Jedis;

import java.util.List;

public abstract class CommentCacheProvider extends CacheProvider {

    private final IUserCommentLikeSetOperation userCommentOperation;
    private final CommentToLikeSetOperation commentOperation;

    protected CommentCacheProvider(IUserCommentLikeSetOperation userCommentOperation,
                                   CommentToLikeSetOperation commentOperation) {
        this.userCommentOperation = userCommentOperation;
        this.commentOperation = commentOperation;
    }

    public <C extends Comment> void provide(List<C> comments, long userId, Jedis jedis) {
        final String userIdStr = String.valueOf(userId);
        super.provide(comments, userIdStr, userCommentOperation, commentOperation, jedis);
    }

    public <C extends Comment> void provide(List<C> comments, String userId, Jedis jedis) {
        super.provide(comments, userId, userCommentOperation, commentOperation, jedis);
    }
}
