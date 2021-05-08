package util.dao.query.builder.redis;

import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;

public class RedisQueryBuilder {

    private final JedisResourceCenter jedisResourceCenter;
    private JedisResource jedisResource;

    private QuestionLikeQueryBuilder questionLikeQueryBuilder;
    private AnswerLikeQueryBuilder answerLikeQueryBuilder;
    private CommentQuestionLikeQueryBuilder commentQuestionLikeQueryBuilder;
    private CommentAnswerLikeQueryBuilder commentAnswerLikeQueryBuilder;

    public RedisQueryBuilder(JedisResourceCenter jedisResourceCenter) {
        this.jedisResourceCenter = jedisResourceCenter;
    }

    public RedisQueryBuilder openJedis() {
        this.jedisResource = jedisResourceCenter.getResource();
        this.questionLikeQueryBuilder = new QuestionLikeQueryBuilder(jedisResource.getJedis());
        this.answerLikeQueryBuilder = new AnswerLikeQueryBuilder(jedisResource.getJedis());
        this.commentQuestionLikeQueryBuilder = new CommentQuestionLikeQueryBuilder(jedisResource.getJedis());
        this.commentAnswerLikeQueryBuilder = new CommentAnswerLikeQueryBuilder(jedisResource.getJedis());
        return this;
    }

    public void closeJedis() {
        this.jedisResource.close();
    }

    public RedisQueryBuilder question(long questionId) {
        this.questionLikeQueryBuilder.create(String.valueOf(questionId));
        return this;
    }

    public RedisQueryBuilder question() {
        this.questionLikeQueryBuilder.create("1");
        return this;
    }

    public RedisQueryBuilder questionLikeIncr(long id) {
        this.questionLikeQueryBuilder.like(id);
        return this;
    }

    public RedisQueryBuilder questionLikeIncr() {
        this.questionLikeQueryBuilder.like(1L);
        return this;
    }

    public RedisQueryBuilder answer(long answerId) {
        this.answerLikeQueryBuilder.create(String.valueOf(answerId));
        return this;
    }

    public RedisQueryBuilder answer() {
        this.answerLikeQueryBuilder.create("1");
        return this;
    }

    public RedisQueryBuilder answerLikeIncr(long answerId) {
        this.answerLikeQueryBuilder.like(answerId);
        return this;
    }

    public RedisQueryBuilder answerLikeIncr() {
        this.answerLikeQueryBuilder.like(1L);
        return this;
    }

    public RedisQueryBuilder commentQuestion(long commentId) {
        this.commentQuestionLikeQueryBuilder.create(String.valueOf(commentId));
        return this;
    }

    public RedisQueryBuilder commentQuestion() {
        this.commentQuestionLikeQueryBuilder.create("1");
        return this;
    }

    public RedisQueryBuilder commentQuestionLikeIncr(long commentId) {
        this.commentQuestionLikeQueryBuilder.like(commentId);
        return this;
    }

    public RedisQueryBuilder commentQuestionLikeIncr() {
        this.commentQuestionLikeQueryBuilder.like(1L);
        return this;
    }

    public RedisQueryBuilder commentAnswer(long commentId) {
        this.commentAnswerLikeQueryBuilder.create(String.valueOf(commentId));
        return this;
    }

    public RedisQueryBuilder commentAnswer() {
        this.commentAnswerLikeQueryBuilder.create("1");
        return this;
    }

    public RedisQueryBuilder commentAnswerLikeIncr(long commentId) {
        this.commentAnswerLikeQueryBuilder.like(commentId);
        return this;
    }

    public RedisQueryBuilder commentAnswerLikeIncr() {
        this.commentAnswerLikeQueryBuilder.like(1L);
        return this;
    }
}

