package qa.cache.operation;

import redis.clients.jedis.Jedis;

import java.util.List;

public abstract class SetSizeOperation {

    public String getS(String key, Jedis jedis) {
        return jedis.get(key);
    }

    public List<String> getS(List<String> keys, Jedis jedis) {
        return jedis.mget(keys.toArray(String[]::new));
    }
}
