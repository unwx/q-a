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
        questionLikeQueryBuilder.create(String.valueOf(questionId));
        return this;
    }

    public RedisQueryBuilder question() {
        questionLikeQueryBuilder.create("1");
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
        answerLikeQueryBuilder.create(String.valueOf(answerId));
        return this;
    }

    public RedisQueryBuilder answer() {
        answerLikeQueryBuilder.create("1");
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

    public RedisQueryBuilder commentQuestion(long commentId) {
        commentQuestionLikeQueryBuilder.create(String.valueOf(commentId));
        return this;
    }

    public RedisQueryBuilder commentQuestion() {
        commentQuestionLikeQueryBuilder.create("1");
        return this;
    }

    public RedisQueryBuilder commentQuestionLikeIncr(long commentId) {
        commentQuestionLikeQueryBuilder.like(commentId);
        return this;
    }

    public RedisQueryBuilder commentQuestionLikeIncr() {
        commentQuestionLikeQueryBuilder.like(1L);
        return this;
    }

    public RedisQueryBuilder commentAnswer(long commentId) {
        commentAnswerLikeQueryBuilder.create(String.valueOf(commentId));
        return this;
    }

    public RedisQueryBuilder commentAnswer() {
        commentAnswerLikeQueryBuilder.create("1");
        return this;
    }

    public RedisQueryBuilder commentAnswerLikeIncr(long commentId) {
        commentAnswerLikeQueryBuilder.like(commentId);
        return this;
    }

    public RedisQueryBuilder commentAnswerLikeIncr() {
        commentAnswerLikeQueryBuilder.like(1L);
        return this;
    }
}

