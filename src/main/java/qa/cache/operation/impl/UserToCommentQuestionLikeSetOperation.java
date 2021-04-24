package qa.cache.operation.impl;

import qa.cache.entity.like.set.UserToCommentQuestionLikeSet;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.cache.operation.UserToEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

public class UserToCommentQuestionLikeSetOperation  extends UserToEntityLikeSetOperation implements IUserToEntitySetOperation<Long> {

    public UserToCommentQuestionLikeSetOperation(Jedis jedis) {
        super(jedis);
    }

    @Override
    public boolean add(long userId, Long entityId) {
        final UserToCommentQuestionLikeSet set = new UserToCommentQuestionLikeSet(userId, entityId);
        return super.add(set);
    }

    @Override
    public boolean isValueExist(long userId, Long entityId) {
        final UserToCommentQuestionLikeSet set = new UserToCommentQuestionLikeSet(userId, entityId);
        return super.isValueExist(set);
    }

    @Override
    public boolean deleteValue(long userId, Long entityId) {
        final UserToCommentQuestionLikeSet set = new UserToCommentQuestionLikeSet(userId, entityId);
        return super.deleteValue(set);
    }

    @Override
    public boolean deleteKey(long userId) {
        final UserToCommentQuestionLikeSet set = new UserToCommentQuestionLikeSet(userId, -1L);
        return super.deleteKey(set);
    }
}
