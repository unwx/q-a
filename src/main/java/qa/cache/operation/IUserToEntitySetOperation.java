package qa.cache.operation;

import redis.clients.jedis.Jedis;

public interface IUserToEntitySetOperation<T> {

    boolean add(long userId, T entityId, Jedis jedis);

    boolean isValueExist(long userId, T entityId, Jedis jedis);

    boolean deleteValue(long userId, T entityId, Jedis jedis);

    boolean deleteKey(long userId, Jedis jedis);
}