package qa.cache.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.JedisFactory;
import qa.cache.like.UserToQuestionLikeSet;
import redis.clients.jedis.Jedis;

@Component
public class UserToQuestionLikeSetOperation {

    private final Jedis jedis;

    @Autowired
    public UserToQuestionLikeSetOperation(JedisFactory jedisFactory) {
        this.jedis = jedisFactory.getJedis();
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
