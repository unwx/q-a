package qa.cache.operation;

import redis.clients.jedis.Jedis;

import java.util.List;

public interface LikeSetOperation {
    boolean create(KeyOperation r, Jedis jedis);

    int getK(KeyOperation r, Jedis jedis);

    List<Integer> getK(List<KeyOperation> r, Jedis jedis);

    long increment(KeyOperation r, Jedis jedis);

    boolean delete(KeyOperation r, Jedis jedis);
}