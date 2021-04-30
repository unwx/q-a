package qa.cache.like.remover;

import qa.cache.like.operation.CommentToLikeSetOperation;
import qa.cache.like.operation.IUserCommentLikeSetOperation;
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
