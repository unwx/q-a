package qa.cache.like.remover;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import qa.cache.like.operation.EntityToLikeSetOperation;
import qa.cache.like.operation.IUserEntityLikeSetOperation;
import qa.cache.like.operation.impl.CacheLikeOperation;
import redis.clients.jedis.Jedis;

import java.util.Stack;

@Component
public abstract class CacheRemover {

    private static final String ERR_CANNOT_DELETE = "cannot delete like cache, id: %s, names: %s";
    private static final Logger logger = LogManager.getLogger(CacheRemover.class);

    protected boolean remove(CacheRemoveInstruction instruction, Jedis jedis) {
        final CacheLikeOperation operation = instruction.getOperation();
        final Stack<String> ids = instruction.getIds();
        return this.removeIteration(operation, ids, jedis);
    }

    protected boolean remove(CacheLikeOperation operation, String id, Jedis jedis) {
        return this.removeIteration(operation, id, jedis);
    }

    private boolean removeIteration(CacheLikeOperation operation, Stack<String> ids, Jedis jedis) {
        if (ids.isEmpty())
            return true;

        final IUserEntityLikeSetOperation userEntityOperation = operation.getUserEntitySetOperation();
        final EntityToLikeSetOperation entityOperation = operation.getEntityToLikeSetOperation();

        while (ids.size() > 0) {
            final String id = ids.pop();
            final boolean status = this.deleteProcess(
                    id,
                    userEntityOperation,
                    entityOperation,
                    jedis
            );
            if (!status) return false;
        }
        return true;
    }

    private boolean removeIteration(CacheLikeOperation operation, String id, Jedis jedis) {
        final IUserEntityLikeSetOperation userEntityOperation = operation.getUserEntitySetOperation();
        final EntityToLikeSetOperation entityOperation = operation.getEntityToLikeSetOperation();

        return this.deleteProcess(id, userEntityOperation, entityOperation, jedis);
    }

    private boolean deleteProcess(String id,
                                  IUserEntityLikeSetOperation userEntityOperation,
                                  EntityToLikeSetOperation entityOperation,
                                  Jedis jedis) {

        final boolean status = entityOperation.delete(id, jedis);
        if (status) {
            userEntityOperation.deleteEntity(id, jedis);
        } else {
            logger.error(ERR_CANNOT_DELETE.formatted(id, entityOperation.name()));
            return false;
        }
        return true;
    }
}
