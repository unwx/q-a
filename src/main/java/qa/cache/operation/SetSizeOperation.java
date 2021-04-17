package qa.cache.operation;

import redis.clients.jedis.Jedis;

import java.util.List;

public abstract class SetSizeOperation {

    private final Jedis jedis;

    public SetSizeOperation(Jedis jedis) {
        this.jedis = jedis;
    }

    public String getS(String key) {
        return jedis.get(key);
    }

    public List<String> getS(List<String> keys) {
        return jedis.mget(keys.toArray(String[]::new));
    }
}
