package qa.cache.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.JedisFactory;
import qa.cache.entity.like.UserToQuestionLikeSet;
import qa.cache.size.QuestionToSizeSet;
import redis.clients.jedis.Jedis;

@Component
public class UserToQuestionLikeSetOperation {

    private final Jedis jedis;
    private final QuestionToSizeSetOperation questionToSizeSetOperation;

    @Autowired
    public UserToQuestionLikeSetOperation(JedisFactory jedisFactory,
                                          QuestionToSizeSetOperation questionToSizeSetOperation) {
        this.jedis = jedisFactory.getJedis();
        this.questionToSizeSetOperation = questionToSizeSetOperation;
    }

    public Long add(UserToQuestionLikeSet like) {
        Long reply = jedis.setnx(like.getKey(), like.getValue());
        if (reply == 0)
            return 0L;
        return questionToSizeSetOperation.increment(new QuestionToSizeSet(like.getClearKey()));
    }

    // get can be implemented

    public Long deleteValue(UserToQuestionLikeSet like) {
        return jedis.srem(like.getKey(), like.getValue());
    }

    public Long deleteKey(UserToQuestionLikeSet like) {
        return jedis.del(like.getKey());
    }
}
