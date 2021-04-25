package qa.cache.operation.impl;

import qa.cache.RedisKeys;
import qa.cache.entity.like.set.AnswerToUserLikeSet;
import qa.cache.entity.like.set.UserToAnswerLikeSet;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.cache.operation.UserToEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

public class UserAnswerLikeSetOperation extends UserToEntityLikeSetOperation implements IUserToEntitySetOperation<Long> {

    @Override
    public boolean add(long userId, Long answerId, Jedis jedis) {
        final UserToAnswerLikeSet userAnswerSet = new UserToAnswerLikeSet(userId, answerId);
        final AnswerToUserLikeSet answerUserSet = new AnswerToUserLikeSet(answerId, userId);

        final boolean reply = super.add(userAnswerSet, jedis);
        if (reply) super.add(answerUserSet, jedis);
        return reply;
    }

    @Override
    public boolean isValueExist(long userId, Long answerId, Jedis jedis) {
        final UserToAnswerLikeSet set = new UserToAnswerLikeSet(userId, answerId);
        return super.isValueExist(set, jedis);
    }

    @Override
    public boolean deleteEntity(Long answerId, Jedis jedis) {
        final AnswerToUserLikeSet answerUserSet = new AnswerToUserLikeSet(answerId, -1L);
        return super.deleteLinks(
                answerUserSet.getKey(),
                answerUserSet.getAnswerId(),
                RedisKeys.USER_ANSWER_LIKES,
                jedis
        );
    }
}