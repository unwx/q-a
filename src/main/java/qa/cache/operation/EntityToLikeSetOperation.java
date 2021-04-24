package qa.cache.operation;

import redis.clients.jedis.Jedis;

import java.util.List;

public interface EntityToLikeSetOperation<T> {
    boolean create(T t, Jedis jedis);

    int get(T t, Jedis jedis);

    List<Integer> get(List<T> t, Jedis jedis);

    long increment(T t, Jedis jedis);

    boolean delete(T t, Jedis jedis);
}
