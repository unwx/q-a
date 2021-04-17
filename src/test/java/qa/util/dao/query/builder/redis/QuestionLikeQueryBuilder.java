package qa.util.dao.query.builder.redis;

import qa.cache.RedisKeys;
import redis.clients.jedis.Jedis;

public class QuestionLikeQueryBuilder {

    private final Jedis jedis;

    public QuestionLikeQueryBuilder(Jedis jedis) {
        this.jedis = jedis;
    }

    public void create(Long questionId) {
        jedis.setnx(RedisKeys.getQuestionLikes(String.valueOf(questionId)), "0");
    }

    public void like(Long questionId) {
        jedis.incr(RedisKeys.getQuestionLikes(String.valueOf(questionId)));
    }
}
