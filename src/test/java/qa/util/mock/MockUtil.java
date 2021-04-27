package qa.util.mock;

import org.mockito.Mockito;
import qa.cache.CacheRemover;
import qa.cache.JedisResourceCenter;
import qa.cache.entity.like.provider.AnswerCacheProvider;
import qa.cache.entity.like.provider.CommentAnswerCacheProvider;
import qa.cache.entity.like.provider.CommentQuestionCacheProvider;
import qa.cache.entity.like.provider.QuestionCacheProvider;
import qa.cache.entity.like.provider.like.AnswerLikeProvider;
import qa.cache.entity.like.provider.like.CommentAnswerLikeProvider;
import qa.cache.entity.like.provider.like.CommentQuestionLikeProvider;
import qa.cache.entity.like.provider.like.QuestionLikesProvider;
import qa.cache.operation.impl.*;
import qa.config.RedisConfiguration;
import qa.source.PasswordPropertyDataSource;

public class MockUtil { // TODO REFACTOR

    private static RedisConfiguration redisConfiguration;
    private static JedisResourceCenter jedisResourceCenter;

    private static CacheRemover cacheRemover;
    private static QuestionCacheProvider questionCacheProvider;
    private static AnswerCacheProvider answerCacheProvider;
    private static CommentQuestionCacheProvider commentQuestionCacheProvider;
    private static CommentAnswerCacheProvider commentAnswerCacheProvider;

    private static QuestionLikesProvider questionLikesProvider;
    private static AnswerLikeProvider answerLikeProvider;
    private static CommentQuestionLikeProvider commentQuestionLikeProvider;
    private static CommentAnswerLikeProvider commentAnswerLikeProvider;

    private static final QuestionToLikeSetOperation            questionLikeOperation;
    private static final AnswerToLikeSetOperation              answerLikeOperation;
    private static final CommentQuestionToLikeSetOperation     commentQuestionLikeOperation;
    private static final CommentAnswerToLikeSetOperation       commentAnswerLikeOperation;
    private static final UserQuestionLikeSetOperation          userQuestionLikeOperation;
    private static final UserAnswerLikeSetOperation            userAnswerLikeOperation;
    private static final UserCommentQuestionLikeSetOperation   userCommentQuestionLikeOperation;
    private static final UserCommentAnswerLikeSetOperation     userCommentAnswerLikeOperation;

    static {
        questionLikeOperation               = Mockito.spy(QuestionToLikeSetOperation.class);
        answerLikeOperation                 = Mockito.spy(AnswerToLikeSetOperation.class);
        commentQuestionLikeOperation        = Mockito.spy(CommentQuestionToLikeSetOperation.class);
        commentAnswerLikeOperation          = Mockito.spy(CommentAnswerToLikeSetOperation.class);
        userQuestionLikeOperation           = Mockito.spy(UserQuestionLikeSetOperation.class);
        userAnswerLikeOperation             = Mockito.spy(UserAnswerLikeSetOperation.class);
        userCommentQuestionLikeOperation    = Mockito.spy(UserCommentQuestionLikeSetOperation.class);
        userCommentAnswerLikeOperation      = Mockito.spy(UserCommentAnswerLikeSetOperation.class);
    }

    private MockUtil() {
    }

    public static JedisResourceCenter mockJedisCenter() {
        if (redisConfiguration == null)
            redisConfiguration = Mockito.spy(new RedisConfiguration(mockPPDataSource()));
        if (jedisResourceCenter == null)
            jedisResourceCenter = new JedisResourceCenter(redisConfiguration);
        return jedisResourceCenter;
    }

    public static CacheRemover mockCacheRemover() { // TODO refactor
        if (cacheRemover == null) {
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

    public static CommentQuestionCacheProvider mockCommentQuestionCacheProvider() {
        if (commentQuestionCacheProvider == null) {
            commentQuestionCacheProvider = new CommentQuestionCacheProvider(
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
        return commentQuestionCacheProvider;
    }

    public static CommentAnswerCacheProvider mockCommentAnswerCacheProvider() {
        if (commentAnswerCacheProvider == null) {
            commentAnswerCacheProvider = new CommentAnswerCacheProvider(
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
        return commentAnswerCacheProvider;
    }


    public static QuestionLikesProvider mockQuestionLikeProvider() {
        if (questionLikesProvider == null) {
            questionLikesProvider = new QuestionLikesProvider(
                    userQuestionLikeOperation,
                    questionLikeOperation
            );
        }
        return questionLikesProvider;
    }

    public static AnswerLikeProvider mockAnswerLikeProvider() {
        if (answerLikeProvider == null) {
            answerLikeProvider = new AnswerLikeProvider(
                    userAnswerLikeOperation,
                    answerLikeOperation
            );
        }
        return answerLikeProvider;
    }

    public static CommentQuestionLikeProvider mockCommentQuestionLikeProvider() {
        if (commentQuestionLikeProvider == null) {
            commentQuestionLikeProvider = new CommentQuestionLikeProvider(
                    userCommentQuestionLikeOperation,
                    commentQuestionLikeOperation
            );
        }
        return commentQuestionLikeProvider;
    }

    public static CommentAnswerLikeProvider mockCommentAnswerLikeProvider() {
        if (commentAnswerLikeProvider == null) {
            commentAnswerLikeProvider = new CommentAnswerLikeProvider(
                    userCommentAnswerLikeOperation,
                    commentAnswerLikeOperation
            );
        }
        return commentAnswerLikeProvider;
    }


    private static PasswordPropertyDataSource mockPPDataSource() {
        PasswordPropertyDataSource propertyDataSource = Mockito.mock(PasswordPropertyDataSource.class);
        Mockito.lenient().when(propertyDataSource.getREDIS_PASSWORD_PATH()).thenReturn("/disk/main/forProjects/qa/redis/password.pass");
        return propertyDataSource;
    }
}
