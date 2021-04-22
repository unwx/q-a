package qa.cache.operation;

import qa.cache.entity.like.UserToQuestionLikeSet;
import redis.clients.jedis.Jedis;

public class UserToQuestionLikeSetOperation {

    private final Jedis jedis;

    public UserToQuestionLikeSetOperation(Jedis jedis) {
        this.jedis = jedis;
    }

    public Long add(UserToQuestionLikeSet like) {
        return jedis.setnx(like.getKey(), like.getValue());
    }

    // get can be implemented

    public Long deleteValue(UserToQuestionLikeSet like) {
        return jedis.srem(like.getKey(), like.getValue());
    }

    public Long deleteKey(UserToQuestionLikeSet like) {
        return jedis.del(like.getKey());
    }
}
