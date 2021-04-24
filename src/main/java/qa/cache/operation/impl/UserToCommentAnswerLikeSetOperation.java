package qa.cache.operation.impl;

import qa.cache.entity.like.set.UserToCommentAnswerLikeSet;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.cache.operation.UserToEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

public class UserToCommentAnswerLikeSetOperation extends UserToEntityLikeSetOperation implements IUserToEntitySetOperation<Long> {

    @Override
    public boolean add(long userId, Long entityId, Jedis jedis) {
        final UserToCommentAnswerLikeSet set = new UserToCommentAnswerLikeSet(userId, entityId);
        return super.add(set, jedis);
    }

    @Override
    public boolean isValueExist(long userId, Long entityId, Jedis jedis) {
        final UserToCommentAnswerLikeSet set = new UserToCommentAnswerLikeSet(userId, entityId);
        return super.isValueExist(set, jedis);
    }

    @Override
    public boolean deleteValue(long userId, Long entityId, Jedis jedis) {
        final UserToCommentAnswerLikeSet set = new UserToCommentAnswerLikeSet(userId, entityId);
        return super.deleteValue(set, jedis);
    }

    @Override
    public boolean deleteKey(long userId, Jedis jedis) {
        final UserToCommentAnswerLikeSet set = new UserToCommentAnswerLikeSet(userId, -1L);
        return super.deleteKey(set, jedis);
    }
}