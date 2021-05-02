package qa.cache.like.remover;

import qa.cache.like.operation.EntityToLikeSetOperation;
import qa.cache.like.operation.IUserEntityLikeSetOperation;
import qa.cache.like.operation.impl.CacheLikeOperation;
import redis.clients.jedis.Jedis;

import java.util.Stack;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class EntityCacheRemover extends CacheRemover {

    private final IUserEntityLikeSetOperation userEntityOperation;
    private final EntityToLikeSetOperation entityOperation;

    protected EntityCacheRemover(IUserEntityLikeSetOperation userEntityOperation,
                                 EntityToLikeSetOperation entityOperation) {
        this.userEntityOperation = userEntityOperation;
        this.entityOperation = entityOperation;
    }

    protected boolean removeEntities(Stack<String> entityIds, Jedis jedis) {
        final CacheLikeOperation operation = new CacheLikeOperation(userEntityOperation, entityOperation);

        final CacheRemoveInstruction instruction = new CacheRemoveInstruction(operation, entityIds);
        return super.remove(instruction, jedis);
    }

    protected boolean removeEntity(String entityId, Jedis jedis) {
        final CacheLikeOperation operation = new CacheLikeOperation(userEntityOperation, entityOperation);
        return super.remove(operation, entityId, jedis);
    }
}
