package qa.cache.operation.impl;

import qa.cache.entity.like.set.UserToAnswerLikeSet;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.cache.operation.UserToEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

public class UserToAnswerLikeSetOperation extends UserToEntityLikeSetOperation implements IUserToEntitySetOperation<Long> {

    @Override
    public boolean add(long userId, Long answerId, Jedis jedis) {
        final UserToAnswerLikeSet set = new UserToAnswerLikeSet(userId, answerId);
        return super.add(set, jedis);
    }

    @Override
    public boolean isValueExist(long userId, Long answerId, Jedis jedis) {
        final UserToAnswerLikeSet set = new UserToAnswerLikeSet(userId, answerId);
        return super.isValueExist(set, jedis);
    }

    @Override
    public boolean deleteValue(long userId, Long answerId, Jedis jedis) {
        final UserToAnswerLikeSet set = new UserToAnswerLikeSet(userId, answerId);
        return super.deleteValue(set, jedis);
    }

    @Override
    public boolean deleteKey(long userId, Jedis jedis) {
        final UserToAnswerLikeSet set = new UserToAnswerLikeSet(userId, -1L);
        return super.deleteKey(set, jedis);
    }
}