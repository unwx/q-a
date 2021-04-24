package qa.cache.operation;

import redis.clients.jedis.Jedis;

public abstract class UserToEntityLikeSetOperation {

    protected boolean add(KeyValueOperation like, Jedis jedis) {
        return jedis.setnx(like.getKey(), like.getValue()) == 1;
    }

    protected boolean isValueExist(KeyValueOperation like, Jedis jedis) {
        final boolean status = jedis.setnx(like.getKey(), like.getValue()) == 0; // create
        if (!status) this.deleteKey(like, jedis); // if created - delete
        return status;
    }

    protected boolean deleteValue(KeyValueOperation like, Jedis jedis) {
        return jedis.srem(like.getKey(), like.getValue()) == 1;
    }

    protected boolean deleteKey(KeyValueOperation like, Jedis jedis) {
        return jedis.del(like.getKey()) == 1;
    }
}