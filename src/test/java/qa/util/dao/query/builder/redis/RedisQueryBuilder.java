package qa.util.dao.query.builder.redis;

import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;

public class RedisQueryBuilder {

    private final JedisResourceCenter jedisResourceCenter;
    private JedisResource jedisResource;

    private QuestionLikeQueryBuilder questionLikeQueryBuilder;

    public RedisQueryBuilder(JedisResourceCenter jedisResourceCenter) {
        this.jedisResourceCenter = jedisResourceCenter;
    }

    public RedisQueryBuilder openJedis() {
        this.jedisResource = jedisResourceCenter.getResource();
        this.questionLikeQueryBuilder = new QuestionLikeQueryBuilder(jedisResource.getJedis());
        return this;
    }

    public void closeJedis() {
        this.jedisResource.close();
    }

    public RedisQueryBuilder question(long questionId) {
        questionLikeQueryBuilder.create(questionId);
        return this;
    }

    public RedisQueryBuilder question() {
        questionLikeQueryBuilder.create(1L);
        return this;
    }

    public RedisQueryBuilder questionLike(long id) {
        questionLikeQueryBuilder.like(id);
        return this;
    }

    public RedisQueryBuilder questionLike() {
        questionLikeQueryBuilder.like(1L);
        return this;
    }
}
