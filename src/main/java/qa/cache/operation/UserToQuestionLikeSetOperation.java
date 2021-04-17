package qa.cache.operation;

import qa.cache.entity.like.UserToQuestionLikeSet;
import qa.cache.size.QuestionToLikeSet;
import redis.clients.jedis.Jedis;

public class UserToQuestionLikeSetOperation {

    private final Jedis jedis;

    public UserToQuestionLikeSetOperation(Jedis jedis) {
        this.jedis = jedis;
    }

    public Long add(UserToQuestionLikeSet like) {
        QuestionLikesOperation questionLikesOperation = new QuestionLikesOperation(jedis);
        Long reply = jedis.setnx(like.getKey(), like.getValue());
        if (reply == 0)
            return 0L;
        return questionLikesOperation.increment(new QuestionToLikeSet(like.getClearKey()));
    }

    // get can be implemented

    public Long deleteValue(UserToQuestionLikeSet like) {
        return jedis.srem(like.getKey(), like.getValue());
    }

    public Long deleteKey(UserToQuestionLikeSet like) {
        return jedis.del(like.getKey());
    }
}
