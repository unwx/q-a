package qa.cache.entity.like.provider.like;

import qa.cache.entity.like.provider.cache.CommentCacheProvider;
import qa.cache.operation.CommentToLikeSetOperation;
import qa.cache.operation.IUserCommentLikeSetOperation;
import qa.domain.Comment;
import redis.clients.jedis.Jedis;

import java.util.List;

public abstract class CommentLikeProvider extends LikesProvider {

    private final IUserCommentLikeSetOperation userCommentOperation;
    private final CommentToLikeSetOperation commentOperation;
    private final CommentCacheProvider cacheProvider;

    protected CommentLikeProvider(IUserCommentLikeSetOperation userCommentOperation,
                                  CommentToLikeSetOperation commentOperation,
                                  CommentCacheProvider cacheProvider) {
        this.userCommentOperation = userCommentOperation;
        this.commentOperation = commentOperation;
        this.cacheProvider = cacheProvider;
    }

    public void initLike(long commentId,
                         Jedis jedis) {

        final String commentIdStr = String.valueOf(commentId);
        super.initLike(commentIdStr, commentOperation, jedis);
    }

    public void like(long userId,
                     long commentId,
                     Jedis jedis) {

        final String userIdStr = String.valueOf(userId);
        final String commentIdStr = String.valueOf(commentId);

        super.like(userIdStr, commentIdStr, userCommentOperation, commentOperation, jedis);
    }

    public <C extends Comment> void provide(List<C> comments, long userId, Jedis jedis) {
        this.cacheProvider.provide(comments, userId, jedis);
    }
}
