package qa.cache.operation;

import redis.clients.jedis.Jedis;

public interface IUserToEntitySetOperation<T> { // TODO make static

    boolean add(long userId, T entityId, Jedis jedis);

    boolean isValueExist(long userId, T entityId, Jedis jedis);

    boolean deleteEntity(T entityId, Jedis jedis);
}