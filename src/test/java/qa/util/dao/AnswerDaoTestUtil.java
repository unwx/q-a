package qa.util.dao;

import org.hibernate.SessionFactory;
import qa.cache.JedisResourceCenter;
import qa.util.dao.query.builder.QueryBuilder;
import qa.util.dao.query.builder.redis.RedisQueryBuilder;

import java.util.Date;

public class AnswerDaoTestUtil {

    private static final long dateAtMillisDefault = 360000000000L;
    public static final int COMMENT_RESULT_SIZE = 3;

    private final QueryBuilder queryBuilder;
    private final RedisQueryBuilder redisQueryBuilder;

    public AnswerDaoTestUtil(SessionFactory sessionFactory, JedisResourceCenter jedisResourceCenter) {
        this.queryBuilder = new QueryBuilder(sessionFactory);
        this.redisQueryBuilder = new RedisQueryBuilder(jedisResourceCenter);
    }

    public void createAnswer() {
        queryBuilder
                .openSession()
                .user()
                .question()
                .answer()
                .closeSession();
        redisQueryBuilder
                .openJedis()
                .answer()
                .closeJedis();
    }

    public void createAnswerNoUser(Boolean answered) {
        queryBuilder
                .openSession()
                .question()
                .answer(1L, answered)
                .closeSession();
        redisQueryBuilder
                .openJedis()
                .answer()
                .closeJedis();
    }

    public void createAnswerNoUser() {
        queryBuilder
                .openSession()
                .question()
                .answer(1L)
                .closeSession();
        redisQueryBuilder
                .openJedis()
                .answer()
                .closeJedis();
    }

    public void createManyAnswers(int answers) {
        queryBuilder
                .openSession()
                .user()
                .question();
        redisQueryBuilder.openJedis();

        for (int i = 0; i < answers; i++) {
            queryBuilder.answer((long) i, new Date(dateAtMillisDefault * i));
            redisQueryBuilder.answer(i);
            if (i % 20 == 0) {
                queryBuilder.flushAndClear();
            }
        }
        queryBuilder.closeSession();
        redisQueryBuilder.closeJedis();
    }

    public void createAnswerWithManyComments(int comments) {
        queryBuilder
                .openSession()
                .user()
                .question()
                .answer();
        redisQueryBuilder.
                openJedis()
                .answer();

        for (int i = 0; i < comments; i++) {
            queryBuilder.commentAnswer((long) i, new Date(dateAtMillisDefault * i));
            redisQueryBuilder.commentAnswer(i);
            if (i % 20 == 0) {
                queryBuilder.flushAndClear();
            }
        }
        queryBuilder.closeSession();
        redisQueryBuilder.closeJedis();
    }

    public void like(long answerId, int times) {
        redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            redisQueryBuilder.answerLikeIncr(answerId);
        }
        redisQueryBuilder.closeJedis();
    }

    public void like(int times) {
        redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            redisQueryBuilder.answerLikeIncr();
        }
        redisQueryBuilder.closeJedis();
    }
}

