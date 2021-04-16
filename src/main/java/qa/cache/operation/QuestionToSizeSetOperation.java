package qa.cache.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.JedisFactory;
import qa.cache.size.QuestionToSizeSet;
import redis.clients.jedis.Jedis;

@Component
public class QuestionToSizeSetOperation {

    private final Jedis jedis;

    @Autowired
    public QuestionToSizeSetOperation(JedisFactory jedisFactory) {
        this.jedis = jedisFactory.getJedis();
    }

    public Long increment(QuestionToSizeSet questionToSizeSet) {
        return jedis.incr(questionToSizeSet.getKey());
    }
}
