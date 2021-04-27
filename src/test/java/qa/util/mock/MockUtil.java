package qa.util.mock;

import org.mockito.Mockito;
import qa.cache.CacheRemover;
import qa.cache.JedisResourceCenter;
import qa.cache.entity.like.provider.AnswerCacheProvider;
import qa.cache.entity.like.provider.QuestionCacheProvider;
import qa.cache.operation.impl.*;
import qa.config.RedisConfiguration;
import qa.source.PasswordPropertyDataSource;

public class MockUtil {

    private static RedisConfiguration redisConfiguration;
    private static JedisResourceCenter jedisResourceCenter;

    private static CacheRemover cacheRemover;
    private static QuestionCacheProvider questionCacheProvider;
    private static AnswerCacheProvider answerCacheProvider;

    private MockUtil() {
    }

    public static JedisResourceCenter mockJedisCenter() {
        if (redisConfiguration == null)
            redisConfiguration = Mockito.spy(new RedisConfiguration(mockPPDataSource()));
        if (jedisResourceCenter == null)
            jedisResourceCenter = new JedisResourceCenter(redisConfiguration);
        return jedisResourceCenter;
    }

    public static CacheRemover mockCacheRemover() {
        if (cacheRemover == null) {
            final QuestionToLikeSetOperation questionLikeOperation = Mockito.spy(QuestionToLikeSetOperation.class);
            final AnswerToLikeSetOperation answerLikeOperation = Mockito.spy(AnswerToLikeSetOperation.class);
            final CommentQuestionToLikeSetOperation commentQuestionLikeOperation = Mockito.spy(CommentQuestionToLikeSetOperation.class);
            final CommentAnswerToLikeSetOperation commentAnswerLikeOperation = Mockito.spy(CommentAnswerToLikeSetOperation.class);
            final UserQuestionLikeSetOperation userQuestionLikeOperation = Mockito.spy(UserQuestionLikeSetOperation.class);
            final UserAnswerLikeSetOperation userAnswerLikeOperation = Mockito.spy(UserAnswerLikeSetOperation.class);
            final UserCommentQuestionLikeSetOperation userCommentQuestionLikeOperation = Mockito.spy(UserCommentQuestionLikeSetOperation.class);
            final UserCommentAnswerLikeSetOperation userCommentAnswerLikeOperation = Mockito.spy(UserCommentAnswerLikeSetOperation.class);
            cacheRemover = new CacheRemover(
                    questionLikeOperation,
                    answerLikeOperation,
                    commentQuestionLikeOperation,
                    commentAnswerLikeOperation,
                    userQuestionLikeOperation,
                    userAnswerLikeOperation,
                    userCommentQuestionLikeOperation,
                    userCommentAnswerLikeOperation
            );
        }
        return cacheRemover;
    }

    public static QuestionCacheProvider mockQuestionCacheProvider() {
        if (questionCacheProvider == null) {
            final QuestionToLikeSetOperation questionLikeOperation = Mockito.spy(QuestionToLikeSetOperation.class);
            final AnswerToLikeSetOperation answerLikeOperation = Mockito.spy(AnswerToLikeSetOperation.class);
            final CommentQuestionToLikeSetOperation commentQuestionLikeOperation = Mockito.spy(CommentQuestionToLikeSetOperation.class);
            final CommentAnswerToLikeSetOperation commentAnswerLikeOperation = Mockito.spy(CommentAnswerToLikeSetOperation.class);
            final UserQuestionLikeSetOperation userQuestionLikeOperation = Mockito.spy(UserQuestionLikeSetOperation.class);
            final UserAnswerLikeSetOperation userAnswerLikeOperation = Mockito.spy(UserAnswerLikeSetOperation.class);
            final UserCommentQuestionLikeSetOperation userCommentQuestionLikeOperation = Mockito.spy(UserCommentQuestionLikeSetOperation.class);
            final UserCommentAnswerLikeSetOperation userCommentAnswerLikeOperation = Mockito.spy(UserCommentAnswerLikeSetOperation.class);
            questionCacheProvider = new QuestionCacheProvider(
                    questionLikeOperation,
                    answerLikeOperation,
                    commentQuestionLikeOperation,
                    commentAnswerLikeOperation,
                    userQuestionLikeOperation,
                    userAnswerLikeOperation,
                    userCommentQuestionLikeOperation,
                    userCommentAnswerLikeOperation
            );
        }
        return questionCacheProvider;
    }

    public static AnswerCacheProvider mockAnswerCacheProvider() {
        if (answerCacheProvider == null) {
            final QuestionToLikeSetOperation questionLikeOperation = Mockito.spy(QuestionToLikeSetOperation.class);
            final AnswerToLikeSetOperation answerLikeOperation = Mockito.spy(AnswerToLikeSetOperation.class);
            final CommentQuestionToLikeSetOperation commentQuestionLikeOperation = Mockito.spy(CommentQuestionToLikeSetOperation.class);
            final CommentAnswerToLikeSetOperation commentAnswerLikeOperation = Mockito.spy(CommentAnswerToLikeSetOperation.class);
            final UserQuestionLikeSetOperation userQuestionLikeOperation = Mockito.spy(UserQuestionLikeSetOperation.class);
            final UserAnswerLikeSetOperation userAnswerLikeOperation = Mockito.spy(UserAnswerLikeSetOperation.class);
            final UserCommentQuestionLikeSetOperation userCommentQuestionLikeOperation = Mockito.spy(UserCommentQuestionLikeSetOperation.class);
            final UserCommentAnswerLikeSetOperation userCommentAnswerLikeOperation = Mockito.spy(UserCommentAnswerLikeSetOperation.class);
            answerCacheProvider = new AnswerCacheProvider(
                    questionLikeOperation,
                    answerLikeOperation,
                    commentQuestionLikeOperation,
                    commentAnswerLikeOperation,
                    userQuestionLikeOperation,
                    userAnswerLikeOperation,
                    userCommentQuestionLikeOperation,
                    userCommentAnswerLikeOperation
            );
        }
        return answerCacheProvider;
    }


    private static PasswordPropertyDataSource mockPPDataSource() {
        PasswordPropertyDataSource propertyDataSource = Mockito.mock(PasswordPropertyDataSource.class);
        Mockito.lenient().when(propertyDataSource.getREDIS_PASSWORD_PATH()).thenReturn("/disk/main/forProjects/qa/redis/password.pass");
        return propertyDataSource;
    }

}
