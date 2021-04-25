package qa.cache.operation.impl;

import qa.cache.entity.like.set.UserToCommentQuestionLikeSet;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.cache.operation.UserToEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

public class UserToCommentQuestionLikeSetOperation extends UserToEntityLikeSetOperation implements IUserToEntitySetOperation<Long> {

    @Override
    public boolean add(long userId, Long entityId, Jedis jedis) {
        final UserToCommentQuestionLikeSet set = new UserToCommentQuestionLikeSet(userId, entityId);
        return super.add(set, jedis);
    }

    @Override
    public boolean isValueExist(long userId, Long entityId, Jedis jedis) {
        final UserToCommentQuestionLikeSet set = new UserToCommentQuestionLikeSet(userId, entityId);
        return super.isValueExist(set, jedis);
    }

    @Override
    public boolean deleteValue(long userId, Long entityId, Jedis jedis) {
        final UserToCommentQuestionLikeSet set = new UserToCommentQuestionLikeSet(userId, entityId);
        return super.deleteValue(set, jedis);
    }

    @Override
    public boolean deleteKey(long userId, Jedis jedis) {
        final UserToCommentQuestionLikeSet set = new UserToCommentQuestionLikeSet(userId, -1L);
        return super.deleteKey(set, jedis);
    }

    @Override
    public boolean deleteEntity(Long entityId, Jedis jedis) {
        return false;
    }
}
