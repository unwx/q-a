package util.dao.query.builder.redis;

import qa.cache.RedisKeys;
import redis.clients.jedis.Jedis;

public class QuestionLikeQueryBuilder {

    private final Jedis jedis;

    public QuestionLikeQueryBuilder(Jedis jedis) {
        this.jedis = jedis;
    }

    public void create(String questionId) {
        jedis.append(RedisKeys.getQuestionLikes(String.valueOf(questionId)), "0");
    }

    public void like(Long questionId) { // incr only
        jedis.incr(RedisKeys.getQuestionLikes(String.valueOf(questionId)));
    }
}
