package qa.cache.operation;

import redis.clients.jedis.Jedis;

@SuppressWarnings("UnusedReturnValue")
public interface IUserEntityLikeSetOperation {

    boolean add(String userId, String entityId, Jedis jedis);

    boolean isValueExist(String userId, String entityId, Jedis jedis);

    boolean deleteEntity(String entityId, Jedis jedis);
}