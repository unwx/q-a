package qa.cache.operation;

import redis.clients.jedis.Jedis;

import java.util.Set;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public abstract class UserEntitySetOperation {

    protected boolean add(KeyValueOperation like, Jedis jedis) {
        return jedis.sadd(like.getKey(), like.getValue()) == 1;
    }

    protected boolean isValueExist(KeyValueOperation like, Jedis jedis) {
        final boolean status = jedis.sadd(like.getKey(), like.getValue()) == 0; // create
        if (!status) jedis.srem(like.getKey(), like.getValue()); // if created - delete
        return status;
    }

    protected boolean deleteLinks(String key, String keyValue, String linkedKeyBeginning, Jedis jedis) {
        final Set<String> set = jedis.smembers(key);
        if (set.isEmpty())
            return false;

        set.forEach((m) -> jedis.srem(linkedKeyBeginning + m, keyValue));
        final Long reply = jedis.del(key);
        return reply != null && reply != 0L;
    }
}