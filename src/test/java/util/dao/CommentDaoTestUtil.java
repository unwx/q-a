package util.dao;

import org.hibernate.SessionFactory;
import qa.cache.JedisResourceCenter;
import util.dao.query.builder.QueryBuilder;
import util.dao.query.builder.redis.RedisQueryBuilder;

import java.util.Date;

public class CommentDaoTestUtil {

    private final QueryBuilder queryBuilder;
    private final RedisQueryBuilder redisQueryBuilder;
    private static final long dateAtMillisDefault = 360000000000L;

    public static final int COMMENT_RESULT_SIZE = 8;

    public CommentDaoTestUtil(SessionFactory sessionFactory,
                              JedisResourceCenter jedisResourceCenter) {
        this.queryBuilder = new QueryBuilder(sessionFactory);
        this.redisQueryBuilder = new RedisQueryBuilder(jedisResourceCenter);
    }

    public void createCommentAnswer() {
        this.queryBuilder
                .openSession()
                .user()
                .question()
                .answer()
                .commentAnswer()
                .closeSession();
        this.redisQueryBuilder
                .openJedis()
                .commentAnswer()
                .closeJedis();
    }

    public void createCommentQuestion() {
        this.queryBuilder
                .openSession()
                .user()
                .question()
                .answer()
                .commentQuestion()
                .closeSession();
        this.redisQueryBuilder
                .openJedis()
                .commentQuestion()
                .closeJedis();
    }

    public void createCommentAnswerNoUser() {
        this.queryBuilder
                .openSession()
                .question()
                .answer()
                .commentAnswer()
                .closeSession();
    }

    public void createCommentQuestionNoUser() {
        this.queryBuilder
                .openSession()
                .question()
                .answer()
                .commentQuestion()
                .closeSession();
        this.redisQueryBuilder
                .openJedis()
                .commentQuestion()
                .closeJedis();
    }

    public void createManyCommentQuestions(int comment) {
        this.queryBuilder
                .openSession()
                .user()
                .question();
        this.redisQueryBuilder.openJedis();

        for (int i = 0; i < comment; i++) {
            this.queryBuilder.commentQuestion((long) i, new Date(dateAtMillisDefault * i));
            this.redisQueryBuilder.commentQuestion(i);
            if (i % 25 == 0)
                queryBuilder.flushAndClear();
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }

    public void createManyCommentAnswers(int comment) {
        this.queryBuilder
                .openSession()
                .user()
                .question()
                .answer();
        this.redisQueryBuilder.openJedis();

        for (int i = 0; i < comment; i++) {
            this.queryBuilder.commentAnswer((long) i, new Date(dateAtMillisDefault * i));
            this.redisQueryBuilder.commentAnswer(i);
            if (i % 25 == 0)
                this.queryBuilder.flushAndClear();
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }


    public void likeCommentQuestion(long commentId, int times) {
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            this.redisQueryBuilder.commentQuestionLikeIncr(commentId);
        }
        this.redisQueryBuilder.closeJedis();
    }

    public void likeCommentQuestion(int times) {
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            this.redisQueryBuilder.commentQuestionLikeIncr();
        }
        this.redisQueryBuilder.closeJedis();
    }

    public void likeCommentAnswer(long commentId, int times) {
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            this.redisQueryBuilder.commentAnswerLikeIncr(commentId);
        }
        this.redisQueryBuilder.closeJedis();
    }

    public void likeCommentAnswer(int times) {
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            this.redisQueryBuilder.commentAnswerLikeIncr();
        }
        this.redisQueryBuilder.closeJedis();
    }
}
