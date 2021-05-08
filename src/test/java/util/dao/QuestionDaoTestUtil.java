package util.dao;

import org.hibernate.SessionFactory;
import qa.cache.JedisResourceCenter;
import util.dao.query.builder.QueryBuilder;
import util.dao.query.builder.redis.RedisQueryBuilder;

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
        this.queryBuilder
                .openSession()
                .user()
                .question();
        this.redisQueryBuilder
                .openJedis()
                .question()
                .closeJedis();

        long commentId = answers;
        for (int i = 0; i < answers; i++) {
            this.queryBuilder
                    .answer((long) i, new Date(i * dateAtMillisDefault))
                    .commentQuestion((long) i, new Date(i * dateAtMillisDefault))
                    .flushAndClear();
            this.redisQueryBuilder
                    .answer(i)
                    .commentQuestion(i);
            for (int y = 0; y < comments; y++) {
                this.queryBuilder.commentAnswer(commentId, (long) i, new Date(y + dateAtMillisDefault));
                this.redisQueryBuilder.commentAnswer(commentId);
                commentId++;
            }
        }
        this.queryBuilder.closeSession();
    }

    public void createQuestionWithComments(int comments) {
        this.queryBuilder
                .openSession()
                .user()
                .question();
        this.redisQueryBuilder
                .openJedis()
                .question();

        for (int i = 0; i < comments; i++) {
            this.queryBuilder.commentQuestion((long) i, new Date(dateAtMillisDefault * i));
            this.redisQueryBuilder.commentQuestion(i);
            if (i % 20 == 0)
                queryBuilder.flushAndClear();
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }

    public void createQuestionWithAnswersWithComments(int answers, int comments) {
        this.queryBuilder
                .openSession()
                .user()
                .question();
        this.redisQueryBuilder
                .openJedis()
                .question();

        long commentId = 0;
        for (int i = 0; i < answers; i++) {
            this.queryBuilder
                    .answer((long) i, new Date(i * dateAtMillisDefault))
                    .flushAndClear();
            this.redisQueryBuilder.answer(i);
            for (int y = 0; y < comments; y++) {
                this.queryBuilder.commentAnswer(commentId, (long) i, new Date(y + dateAtMillisDefault + i * 1000L));
                this.redisQueryBuilder.commentAnswer(commentId);
                commentId++;
            }
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }

    public void createManyQuestionsWithManyAnswers(int questions, int answers) {
        this.queryBuilder
                .openSession()
                .user();
        this.redisQueryBuilder
                .openJedis();

        long answerId = 0;
        for (int i = 0; i < questions; i++) {
            this.queryBuilder
                    .question((long) i, new Date(i * dateAtMillisDefault))
                    .flushAndClear();
            this.redisQueryBuilder
                    .question(i);
            for (int y = 0; y < answers; y++) {
                queryBuilder.answer(answerId, (long) i, new Date(y + dateAtMillisDefault + i * 1000L));
                answerId++;
            }
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }

    public void createManyQuestions(int questions) {
        this.queryBuilder
                .openSession()
                .user();
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < questions; i++) {
            this.queryBuilder.question((long) i, new Date(i * dateAtMillisDefault));
            this.redisQueryBuilder.question(i);
            if (i % 20 == 0) {
                queryBuilder.flushAndClear();
            }
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }

    public void createQuestion() {
        this.queryBuilder
                .openSession()
                .user()
                .question()
                .closeSession();
        this.redisQueryBuilder
                .openJedis()
                .question()
                .closeJedis();
    }

    public void createQuestionNoUser() {
        this.queryBuilder
                .openSession()
                .question()
                .closeSession();
        this.redisQueryBuilder
                .openJedis()
                .question()
                .closeJedis();
    }

    public void like(long questionId, int times) {
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            this.redisQueryBuilder.questionLikeIncr(questionId);
        }
        this.redisQueryBuilder.closeJedis();
    }

    public void like(int times) {
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < times; i++) {
            this.redisQueryBuilder.questionLikeIncr();
        }
        this.redisQueryBuilder.closeJedis();
    }
}
