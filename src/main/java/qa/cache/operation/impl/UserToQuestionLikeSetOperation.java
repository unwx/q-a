package qa.cache.operation.impl;

import qa.cache.entity.like.set.UserToQuestionLikeSet;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.cache.operation.UserToEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

public class UserToQuestionLikeSetOperation extends UserToEntityLikeSetOperation implements IUserToEntitySetOperation<Long> {

    public UserToQuestionLikeSetOperation(Jedis jedis) {
        super(jedis);
    }

    @Override
    public boolean add(long userId, Long questionId) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, questionId);
        return super.add(set);
    }

    @Override
    public boolean isValueExist(long userId, Long questionId) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, questionId);
        return super.isValueExist(set);
    }

    @Override
    public boolean deleteValue(long userId, Long questionId) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, questionId);
        return super.deleteValue(set);
    }

    @Override
    public boolean deleteKey(long userId) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, -1L);
        return super.deleteKey(set);
    }
}
