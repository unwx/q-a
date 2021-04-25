package qa.cache.operation.impl;

import qa.cache.RedisKeys;
import qa.cache.entity.like.set.QuestionToUserLikeSet;
import qa.cache.entity.like.set.UserToQuestionLikeSet;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.cache.operation.UserToEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

public class UserQuestionLikeSetOperation extends UserToEntityLikeSetOperation implements IUserToEntitySetOperation<Long> {

    @Override
    public boolean add(long userId, Long questionId, Jedis jedis) {
        final UserToQuestionLikeSet userQuestionSet = new UserToQuestionLikeSet(userId, questionId);
        final QuestionToUserLikeSet questionUserSet = new QuestionToUserLikeSet(questionId, userId);

        final boolean reply = super.add(userQuestionSet, jedis);
        if (reply) super.add(questionUserSet, jedis);
        return reply;
    }

    @Override
    public boolean isValueExist(long userId, Long questionId, Jedis jedis) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, questionId);
        return super.isValueExist(set, jedis);
    }

    @Override
    @Deprecated
    public boolean deleteValue(long userId, Long questionId, Jedis jedis) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, questionId);
        return super.deleteValue(set, jedis);
    }

    @Override
    @Deprecated
    public boolean deleteKey(long userId, Jedis jedis) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, -1L);
        return super.deleteKey(set, jedis);
    }

    @Override
    public boolean deleteEntity(Long questionId, Jedis jedis) {
        final QuestionToUserLikeSet questionUserSet = new QuestionToUserLikeSet(questionId, -1L);
        return super.deleteLinks(
                questionUserSet.getKey(),
                questionUserSet.getQuestionId(),
                RedisKeys.USER_QUESTION_LIKES,
                jedis
        );
    }
}
