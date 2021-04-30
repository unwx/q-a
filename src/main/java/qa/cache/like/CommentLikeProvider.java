package qa.cache.like;

import qa.cache.like.operation.CommentToLikeSetOperation;
import qa.cache.like.operation.IUserCommentLikeSetOperation;
import qa.cache.like.provider.CommentCacheProvider;
import qa.cache.like.remover.CommentCacheRemover;
import qa.domain.Comment;
import redis.clients.jedis.Jedis;

import java.util.List;

public abstract class CommentLikeProvider extends LikesProvider {

    private final IUserCommentLikeSetOperation userCommentOperation;
    private final CommentToLikeSetOperation commentOperation;
    private final CommentCacheProvider cacheProvider;
    private final CommentCacheRemover cacheRemover;

    protected CommentLikeProvider(IUserCommentLikeSetOperation userCommentOperation,
                                  CommentToLikeSetOperation commentOperation,
                                  CommentCacheProvider cacheProvider,
                                  CommentCacheRemover cacheRemover) {
        this.userCommentOperation = userCommentOperation;
        this.commentOperation = commentOperation;
        this.cacheProvider = cacheProvider;
        this.cacheRemover = cacheRemover;
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

    public void remove(long commentId, Jedis jedis) {
        final String commentIdStr = String.valueOf(commentId);
        this.cacheRemover.remove(commentIdStr, jedis);
    }
}
