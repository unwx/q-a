package qa.cache.operation.impl;

import qa.cache.entity.like.set.UserToQuestionLikeSet;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.cache.operation.UserToEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

public class UserToQuestionLikeSetOperation extends UserToEntityLikeSetOperation implements IUserToEntitySetOperation<Long> {

    @Override
    public boolean add(long userId, Long questionId, Jedis jedis) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, questionId);
        return super.add(set, jedis);
    }

    @Override
    public boolean isValueExist(long userId, Long questionId, Jedis jedis) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, questionId);
        return super.isValueExist(set, jedis);
    }

    @Override
    public boolean deleteValue(long userId, Long questionId, Jedis jedis) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, questionId);
        return super.deleteValue(set, jedis);
    }

    @Override
    public boolean deleteKey(long userId, Jedis jedis) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, -1L);
        return super.deleteKey(set, jedis);
    }
}
