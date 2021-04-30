package qa.cache.like;

import qa.cache.like.operation.EntityToLikeSetOperation;
import qa.cache.like.operation.IUserEntityLikeSetOperation;
import qa.exceptions.dao.EntityAlreadyCreatedException;
import redis.clients.jedis.Jedis;

public abstract class LikesProvider {

    private static final String ERR_ALREADY_EXIST = "entity already exist. id: %s";

    protected boolean like(String userId,
                           String entityId,
                           IUserEntityLikeSetOperation userEntityOperation,
                           EntityToLikeSetOperation entityOperation,
                           Jedis jedis) {


        final boolean status = userEntityOperation.add(userId, entityId, jedis);
        if (status) entityOperation.increment(entityId, jedis);
        return status;
    }

    protected void initLike(String id,
                            EntityToLikeSetOperation entityOperation,
                            Jedis jedis) {

        final boolean reply = entityOperation.create(id, jedis);
        if (!reply) throw new EntityAlreadyCreatedException(ERR_ALREADY_EXIST.formatted(id));
    }
}
