package qa.util.dao.query.builder.redis;

import qa.cache.RedisKeys;
import redis.clients.jedis.Jedis;

public class AnswerLikeQueryBuilder {

    private final Jedis jedis;

    public AnswerLikeQueryBuilder(Jedis jedis) {
        this.jedis = jedis;
    }

    public void create(Long questionId) {
        jedis.setnx(RedisKeys.getAnswerLikes(String.valueOf(questionId)), "0");
    }

    public void like(Long questionId) {
        jedis.incr(RedisKeys.getAnswerLikes(String.valueOf(questionId)));
    }
}
