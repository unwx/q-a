package qa.cache.operation;

import redis.clients.jedis.Jedis;

public abstract class UserToEntityLikeSetOperation {

    private final Jedis jedis;

    protected UserToEntityLikeSetOperation(Jedis jedis) {
        this.jedis = jedis;
    }

    protected boolean add(KeyValueOperation like) {
        return jedis.setnx(like.getKey(), like.getValue()) == 1;
    }

    protected boolean isValueExist(KeyValueOperation like) {
        final boolean status = jedis.setnx(like.getKey(), like.getValue()) == 0; // create
        if (!status) this.deleteKey(like); // if created - delete
        return status;
    }

    protected boolean deleteValue(KeyValueOperation like) {
        return jedis.srem(like.getKey(), like.getValue()) == 1;
    }

    protected boolean deleteKey(KeyValueOperation like) {
        return jedis.del(like.getKey()) == 1;
    }
}