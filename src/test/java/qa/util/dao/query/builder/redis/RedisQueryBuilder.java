package qa.util.dao.query.builder.redis;

import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;

public class RedisQueryBuilder {

    private final JedisResourceCenter jedisResourceCenter;
    private JedisResource jedisResource;

    private QuestionLikeQueryBuilder questionLikeQueryBuilder;
    private AnswerLikeQueryBuilder answerLikeQueryBuilder;

    public RedisQueryBuilder(JedisResourceCenter jedisResourceCenter) {
        this.jedisResourceCenter = jedisResourceCenter;
    }

    public RedisQueryBuilder openJedis() {
        this.jedisResource = jedisResourceCenter.getResource();
        this.questionLikeQueryBuilder = new QuestionLikeQueryBuilder(jedisResource.getJedis());
        this.answerLikeQueryBuilder = new AnswerLikeQueryBuilder(jedisResource.getJedis());
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

    public RedisQueryBuilder questionLikeIncr(long id) {
        questionLikeQueryBuilder.like(id);
        return this;
    }

    public RedisQueryBuilder questionLikeIncr() {
        questionLikeQueryBuilder.like(1L);
        return this;
    }

    public RedisQueryBuilder answer(long answerId) {
        answerLikeQueryBuilder.create(answerId);
        return this;
    }

    public RedisQueryBuilder answer() {
        answerLikeQueryBuilder.create(1L);
        return this;
    }

    public RedisQueryBuilder answerLikeIncr(long answerId) {
        answerLikeQueryBuilder.like(answerId);
        return this;
    }

    public RedisQueryBuilder answerLikeIncr() {
        answerLikeQueryBuilder.like(1L);
        return this;
    }
}

