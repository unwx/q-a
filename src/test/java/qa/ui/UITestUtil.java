package qa.ui;

import util.dao.query.builder.QueryBuilder;
import util.dao.query.builder.redis.RedisQueryBuilder;

import java.util.Date;

public class UITestUtil {

    private static final int startId = 777777;
    private static final long date = 1500000000;

    /*
    * it is recommended to keep the numbers at the same level,
    * because with large scatter,
    * testing becomes more difficult
    * - the data is submitted page by page,
    * so in order not to check each one - use normal coefficients
     */
    private static final int users = 75;
    private static final int questions = 100;
    private static final int answers = 125;
    private static final int questionComments = 100;
    private static final int answerComments = 150;

    private static final long[] usersId = new long[users];
    private static final long[] questionsId = new long[questions];
    private static final long[] answersId = new long[answers];

    private static final StringBuilder sb = new StringBuilder();
    private static final String[] tags = new String[] {
            "java", "spring", "hibernate", "orm", "c", "performance", "data", "exception", "cpp", "pointer", "rest", "css"
    };

    private final QueryBuilder queryBuilder;
    private final RedisQueryBuilder redisQueryBuilder;

    public UITestUtil(QueryBuilder queryBuilder,
                      RedisQueryBuilder redisQueryBuilder) {
        this.queryBuilder = queryBuilder;
        this.redisQueryBuilder = redisQueryBuilder;
    }

    /**
     * creates a common testing environment which includes:
     * - many users
     * - many questions
     * - many answers
     * - many comments
     */
    public void prepare() {
        this.createUsers();
        this.createQuestions();
        this.createAnswers();
        this.createCommentQuestions();
        this.createCommentAnswer();
    }

    private void createUsers() {
        this.queryBuilder.openSession();
        for (int i = 0; i < users; i++) {
            final int id = startId + i;
            final String username = this.generateRandomText(8, 15);
            this.queryBuilder.user(id, username);
            usersId[i] = id;
            flush(i);
        }
        this.queryBuilder.closeSession();
    }

    private void createQuestions() {
        this.queryBuilder.openSession();
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < questions; i++) {
            final int id = startId + i;
            final String title = this.generateRandomText(15, 25);
            final String text = this.generateRandomText(300, 500);
            final String tags = this.generateRandomTags(4);
            final int target = (int) (Math.random() * users);

            this.queryBuilder.question(id, usersId[target], new Date(date * i), tags, text, title);
            this.redisQueryBuilder.question(id);
            questionsId[i] = id;
            flush(i);
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }

    private void createAnswers() {
        this.queryBuilder.openSession();
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < answers; i++) {
            final int id = startId + i;
            final String text = this.generateRandomText(25, 300);
            final int user = (int) (Math.random() * users);
            final int question = (int) (Math.random() * questions);
            this.queryBuilder.answer(id, usersId[user], questionsId[question], text, new Date(date * i));
            this.redisQueryBuilder.answer(id);
            answersId[i] = id;
            flush(i);
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }

    private void createCommentQuestions() {
        this.queryBuilder.openSession();
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < questionComments; i++) {
            final int id = startId - questionComments + i;
            final String text = this.generateRandomText(10, 70);
            final int user = (int) (Math.random() * users);
            final int question = (int) (Math.random() * questions);
            this.queryBuilder.commentQuestion(id, usersId[user], questionsId[question], text, new Date(date * i));
            this.redisQueryBuilder.commentQuestion(id);
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }

    private void createCommentAnswer() {
        this.queryBuilder.openSession();
        this.redisQueryBuilder.openJedis();
        for (int i = 0; i < answerComments; i++) {
            final int id = startId + answerComments + i;
            final String text = this.generateRandomText(10, 70);
            final int user = (int) (Math.random() * users);
            final int answer = (int) (Math.random() * answers);
            this.queryBuilder.commentAnswer(id, usersId[user], answersId[answer], text, new Date(date * i));
            this.redisQueryBuilder.commentAnswer(id);
        }
        this.queryBuilder.closeSession();
        this.redisQueryBuilder.closeJedis();
    }

    private void flush(int i) {
        if (i % 15 == 0)
            queryBuilder.flushAndClear();
    }

    private String generateRandomText(int start, int end) {
        for (int i = 0; i < start + (int) (start + Math.random() * end - start); i++) {
            sb.append((char) (97 + Math.random() * 25));
        }
        final String result = sb.toString();
        sb.setLength(0);
        return result;
    }

    private String generateRandomTags(int count) {
        final int length = tags.length;
        for (int i = 0; i < count; i++) {
            sb
                    .append(tags[(int) (Math.random() * length)])
                    .append(",");
        }
        final String result = sb.toString();
        sb.setLength(0);
        return result;
    }
}
