package qa.util.dao;

import org.hibernate.SessionFactory;
import qa.cache.JedisResourceCenter;
import qa.util.dao.query.builder.QueryBuilder;
import qa.util.dao.query.builder.redis.RedisQueryBuilder;

import java.util.Date;

public class CommentDaoTestUtil {

    private final QueryBuilder queryBuilder;
    private final RedisQueryBuilder redisQueryBuilder;
    private static final long dateAtMillisDefault = 360000000000L;

    public static final int COMMENT_RESULT_SIZE = 3;

    public CommentDaoTestUtil(SessionFactory sessionFactory,
                              JedisResourceCenter jedisResourceCenter) {
        this.queryBuilder = new QueryBuilder(sessionFactory);
        this.redisQueryBuilder = new RedisQueryBuilder(jedisResourceCenter);
    }

    public void createCommentAnswer() {
        queryBuilder
                .openSession()
                .user()
                .question()
                .answer()
                .commentAnswer()
                .closeSession();
    }

    public void createCommentQuestion() {
        queryBuilder
                .openSession()
                .user()
                .question()
                .answer()
                .commentQuestion()
                .closeSession();
        redisQueryBuilder
                .openJedis()
                .commentQuestion()
                .closeJedis();
    }

    public void createCommentAnswerNoUser() {
        queryBuilder
                .openSession()
                .question()
                .answer()
                .commentAnswer()
                .closeSession();
    }

    public void createCommentQuestionNoUser() {
        queryBuilder
                .openSession()
                .question()
                .answer()
                .commentQuestion()
                .closeSession();
        redisQueryBuilder
                .openJedis()
                .commentQuestion()
                .closeJedis();
    }

    public void createManyCommentQuestions(int comment) {
        queryBuilder
                .openSession()
                .user()
                .question();
        redisQueryBuilder.openJedis();

        for (int i = 0; i < comment; i++) {
            queryBuilder.commentQuestion((long) i, new Date(dateAtMillisDefault * i));
            redisQueryBuilder.commentQuestion(i);
            if (i % 25 == 0)
                queryBuilder.flushAndClear();
        }
        queryBuilder.closeSession();
        redisQueryBuilder.closeJedis();
    }

    public void createManyCommentAnswers(int comment) {
        queryBuilder
                .openSession()
                .user()
                .question()
                .answer();
        redisQueryBuilder.openJedis();

        for (int i = 0; i < comment; i++) {
            queryBuilder.commentAnswer((long) i, new Date(dateAtMillisDefault * i));
            redisQueryBuilder.commentQuestion(i);
            if (i % 25 == 0)
                queryBuilder.flushAndClear();
        }
        queryBuilder.closeSession();
        redisQueryBuilder.closeJedis();
    }


    public void like(long questionId, int times) {
        redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            redisQueryBuilder.commentQuestionLikeIncr(questionId);
        }
        redisQueryBuilder.closeJedis();
    }

    public void like(int times) {
        redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            redisQueryBuilder.commentQuestionLikeIncr();
        }
        redisQueryBuilder.closeJedis();
    }
}
