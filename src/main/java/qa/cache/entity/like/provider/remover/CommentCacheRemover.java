package qa.cache.entity.like.provider.remover;

import qa.cache.operation.CommentToLikeSetOperation;
import qa.cache.operation.IUserCommentLikeSetOperation;
import redis.clients.jedis.Jedis;

import java.util.Stack;

public abstract class CommentCacheRemover extends EntityCacheRemover {

    protected CommentCacheRemover(IUserCommentLikeSetOperation userCommentOperation,
                                  CommentToLikeSetOperation commentOperation) {
        super(userCommentOperation, commentOperation);
    }

    public void remove(Stack<String> commentIds, Jedis jedis) {
        super.removeEntities(commentIds, jedis);
    }

    public void remove(String id, Jedis jedis) {
        super.removeEntity(id, jedis);
    }
}
