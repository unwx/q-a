package qa.cache.operation;

import redis.clients.jedis.Jedis;

import java.util.List;

public interface EntityToLikeSetOperation { // TODO RENAME
    boolean create(String id, Jedis jedis);

    int get(String id, Jedis jedis);

    List<Integer> get(List<String> ids, Jedis jedis);

    long increment(String id, Jedis jedis);

    boolean delete(String id, Jedis jedis);
}
