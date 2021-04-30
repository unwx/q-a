package qa.util.dao;

import org.hibernate.SessionFactory;
import qa.cache.JedisResourceCenter;
import qa.util.dao.query.builder.QueryBuilder;
import qa.util.dao.query.builder.redis.RedisQueryBuilder;

import java.util.Date;

public class QuestionDaoTestUtil {

    private static final long dateAtMillisDefault = 360000000000L;

    public static final int COMMENT_RESULT_SIZE = 8;
    public static final int RESULT_SIZE = 8;
    public static final int QUESTION_VIEW_RESULT_SIZE = 20;

    private final QueryBuilder queryBuilder;
    private final RedisQueryBuilder redisQueryBuilder;

    public QuestionDaoTestUtil(SessionFactory sessionFactory,
                               JedisResourceCenter jedisResourceCenter) {
        this.queryBuilder = new QueryBuilder(sessionFactory);
        this.redisQueryBuilder = new RedisQueryBuilder(jedisResourceCenter);
    }

    public void createQuestionWithCommentsAndAnswersWithComments(int answers, int comments) {
        queryBuilder
                .openSession()
                .user()
                .question();
        redisQueryBuilder
                .openJedis()
                .question()
                .closeJedis();

        long commentId = answers;
        for (int i = 0; i < answers; i++) {
            queryBuilder
                    .answer((long) i, new Date(i * dateAtMillisDefault))
                    .commentQuestion((long) i, new Date(i * dateAtMillisDefault))
                    .flushAndClear();
            redisQueryBuilder
                    .answer(i)
                    .commentQuestion(i);
            for (int y = 0; y < comments; y++) {
                queryBuilder.commentAnswer(commentId, (long) i, new Date(y + dateAtMillisDefault));
                redisQueryBuilder.commentAnswer(commentId);
                commentId++;
            }
        }
        queryBuilder.closeSession();
    }

    public void createQuestionWithComments(int comments) {
        queryBuilder
                .openSession()
                .user()
                .question();
        redisQueryBuilder
                .openJedis()
                .question();

        for (int i = 0; i < comments; i++) {
            queryBuilder.commentQuestion((long) i, new Date(dateAtMillisDefault * i));
            redisQueryBuilder.commentQuestion(i);
            if (i % 20 == 0)
                queryBuilder.flushAndClear();
        }
        queryBuilder.closeSession();
        redisQueryBuilder.closeJedis();
    }

    public void createQuestionWithAnswersWithComments(int answers, int comments) {
        queryBuilder
                .openSession()
                .user()
                .question();
        redisQueryBuilder
                .openJedis()
                .question();

        long commentId = 0;
        for (int i = 0; i < answers; i++) {
            queryBuilder
                    .answer((long) i, new Date(i * dateAtMillisDefault))
                    .flushAndClear();
            redisQueryBuilder.answer(i);
            for (int y = 0; y < comments; y++) {
                queryBuilder.commentAnswer(commentId, (long) i, new Date(y + dateAtMillisDefault + i * 1000L));
                redisQueryBuilder.commentAnswer(commentId);
                commentId++;
            }
        }
        queryBuilder.closeSession();
        redisQueryBuilder.closeJedis();
    }

    public void createManyQuestionsWithManyAnswers(int questions, int answers) {
        queryBuilder
                .openSession()
                .user();
        redisQueryBuilder
                .openJedis();

        long answerId = 0;
        for (int i = 0; i < questions; i++) {
            queryBuilder
                    .question((long) i, new Date(i * dateAtMillisDefault))
                    .flushAndClear();
            redisQueryBuilder
                    .question(i);
            for (int y = 0; y < answers; y++) {
                queryBuilder.answer(answerId, (long) i, new Date(y + dateAtMillisDefault + i * 1000L));
                answerId++;
            }
        }
        queryBuilder.closeSession();
        redisQueryBuilder.closeJedis();
    }

    public void createManyQuestions(int questions) {
        queryBuilder
                .openSession()
                .user();
        redisQueryBuilder.openJedis();
        for (int i = 0; i < questions; i++) {
            queryBuilder.question((long) i, new Date(i * dateAtMillisDefault));
            redisQueryBuilder.question(i);
            if (i % 20 == 0) {
                queryBuilder.flushAndClear();
            }
        }
        queryBuilder.closeSession();
        redisQueryBuilder.closeJedis();
    }

    public void createQuestion() {
        queryBuilder
                .openSession()
                .user()
                .question()
                .closeSession();
        redisQueryBuilder
                .openJedis()
                .question()
                .closeJedis();
    }

    public void createQuestionNoUser() {
        queryBuilder
                .openSession()
                .question()
                .closeSession();
        redisQueryBuilder
                .openJedis()
                .question()
                .closeJedis();
    }

    public void like(long questionId, int times) {
        redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            redisQueryBuilder.questionLikeIncr(questionId);
        }
        redisQueryBuilder.closeJedis();
    }

    public void like(int times) {
        redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            redisQueryBuilder.questionLikeIncr();
        }
        redisQueryBuilder.closeJedis();
    }
}
