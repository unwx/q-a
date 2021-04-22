package qa.cache.operation;

import qa.cache.entity.like.UserToQuestionLikeSet;
import redis.clients.jedis.Jedis;

public class UserToQuestionLikeSetOperation extends UserToEntityLikeSetOperation {

    public UserToQuestionLikeSetOperation(Jedis jedis) {
        super(jedis);
    }

    public Long add(long userId, long questionId) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, questionId);
        return super.add(set);
    }

    public boolean isValueExist(long userId, long questionId) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, questionId);
        return super.isValueExist(set);
    }

    public Long deleteValue(long userId, long questionId) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, questionId);
        return super.deleteValue(set);
    }

    public Long deleteKey(long userId) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, -1L);
        return super.deleteKey(set);
    }
}
