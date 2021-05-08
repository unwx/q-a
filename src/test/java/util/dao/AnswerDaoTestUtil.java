package util.dao;

import org.hibernate.SessionFactory;
import qa.cache.JedisResourceCenter;
import util.dao.query.builder.QueryBuilder;
import util.dao.query.builder.redis.RedisQueryBuilder;

import java.util.Date;

public class AnswerDaoTestUtil {

    private static final long dateAtMillisDefault = 360000000000L;
    public static final int COMMENT_RESULT_SIZE = 8;

    private final QueryBuilder queryBuilder;
    private final RedisQueryBuilder redisQueryBuilder;

    public AnswerDaoTestUtil(SessionFactory sessionFactory, JedisResourceCenter jedisResourceCenter) {
        this.queryBuilder = new QueryBuilder(sessionFactory);
        this.redisQueryBuilder = new RedisQueryBuilder(jedisResourceCenter);
    }

    public void createAnswer() {
        this.queryBuilder
                .openSession()
                .user()
                .question()
                .answer()
                .closeSession();
        this.redisQueryBuilder
                .openJedis()
                .answer()
                .closeJedis();
    }

    public void createAnswerNoUser(Boolean answered) {
        this.queryBuilder
                .openSession()
                .question()
                .answer(1L, answered)
                .closeSession();
        this.redisQueryBuilder
                .openJedis()
                .answer()
                .closeJedis();
    }

    public void createAnswerNoUser() {
        this.queryBuilder
                .openSession()
                .question()
                .answer(1L)
                .closeSession();
        this.redisQueryBuilder
                .openJedis()
                .answer()
                .closeJedis();
    }

    public void createManyAnswers(int answers) {
        this.queryBuilder
                .openSession()
                .user()
                .question();
        this.redisQueryBuilder.openJedis();

        for (int i = 0; i < answers; i++) {
            this.queryBuilder.answer((long) i, new Date(dateAtMillisDefault * i));
            this.redisQueryBuilder.answer(i);
            if (i % 20 == 0) {
                this.queryBuilder.flushAndClear();
            }
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }

    public void createManyAnswersWithManyComments(int answers, int comments) {
        this.queryBuilder
                .openSession()
                .user()
                .question();
        this.redisQueryBuilder.openJedis();

        long commentId = 0;
        for (int i = 0; i < answers; i++) {
            this.queryBuilder
                    .answer((long) i, new Date(dateAtMillisDefault * i))
                    .flushAndClear();
            for (int y = 0; y < comments; y++) {
                this.queryBuilder.commentAnswer(commentId, (long) i, new Date(dateAtMillisDefault * i + y));
                this.redisQueryBuilder.commentAnswer(commentId);
                commentId++;
            }
            this.redisQueryBuilder.answer(i);
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }

    public void createAnswerWithManyComments(int comments) {
        this.queryBuilder
                .openSession()
                .user()
                .question()
                .answer();
        this.redisQueryBuilder.
                openJedis()
                .answer();

        for (int i = 0; i < comments; i++) {
            this.queryBuilder.commentAnswer((long) i, new Date(dateAtMillisDefault * i));
            this.redisQueryBuilder.commentAnswer(i);
            if (i % 20 == 0) {
                this.queryBuilder.flushAndClear();
            }
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }

    public void like(long answerId, int times) {
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            this.redisQueryBuilder.answerLikeIncr(answerId);
        }
        this.redisQueryBuilder.closeJedis();
    }

    public void like(int times) {
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            this.redisQueryBuilder.answerLikeIncr();
        }
        this.redisQueryBuilder.closeJedis();
    }
}

