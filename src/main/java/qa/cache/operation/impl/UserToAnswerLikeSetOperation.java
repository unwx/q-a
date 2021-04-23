package qa.cache.operation.impl;

import qa.cache.entity.like.set.UserToAnswerLikeSet;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.cache.operation.UserToEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

public class UserToAnswerLikeSetOperation extends UserToEntityLikeSetOperation implements IUserToEntitySetOperation<Long> {

    public UserToAnswerLikeSetOperation(Jedis jedis) {
        super(jedis);
    }

    @Override
    public boolean add(long userId, Long answerId) {
        final UserToAnswerLikeSet set = new UserToAnswerLikeSet(userId, answerId);
        return super.add(set);
    }

    @Override
    public boolean isValueExist(long userId, Long answerId) {
        final UserToAnswerLikeSet set = new UserToAnswerLikeSet(userId, answerId);
        return super.isValueExist(set);
    }

    @Override
    public boolean deleteValue(long userId, Long answerId) {
        final UserToAnswerLikeSet set = new UserToAnswerLikeSet(userId, answerId);
        return super.deleteValue(set);
    }

    @Override
    public boolean deleteKey(long userId) {
        final UserToAnswerLikeSet set = new UserToAnswerLikeSet(userId, -1L);
        return super.deleteKey(set);
    }
}