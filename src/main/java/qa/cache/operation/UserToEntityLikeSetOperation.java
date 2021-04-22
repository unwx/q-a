package qa.cache.operation;

import qa.cache.KeyValueOperation;
import redis.clients.jedis.Jedis;

public abstract class UserToEntityLikeSetOperation {

    private final Jedis jedis;

    protected UserToEntityLikeSetOperation(Jedis jedis) {
        this.jedis = jedis;
    }

    protected Long add(KeyValueOperation like) {
        return jedis.setnx(like.getKey(), like.getValue());
    }

    protected boolean isValueExist(KeyValueOperation like) {
        final boolean status = jedis.setnx(like.getKey(), like.getValue()) == 0; // create
        if (!status) this.deleteKey(like); // if created - delete
        return status;
    }

    protected Long deleteValue(KeyValueOperation like) {
        return jedis.srem(like.getKey(), like.getValue());
    }

    protected Long deleteKey(KeyValueOperation like) {
        return jedis.del(like.getKey());
    }
}