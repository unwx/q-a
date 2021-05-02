package qa.util.mock;

import org.mockito.Mockito;
import qa.cache.JedisResourceCenter;
import qa.cache.like.AnswerLikeProvider;
import qa.cache.like.CommentAnswerLikeProvider;
import qa.cache.like.CommentQuestionLikeProvider;
import qa.cache.like.QuestionLikesProvider;
import qa.cache.like.operation.impl.*;
import qa.cache.like.provider.AnswerCacheProvider;
import qa.cache.like.provider.CommentAnswerCacheProvider;
import qa.cache.like.provider.CommentQuestionCacheProvider;
import qa.cache.like.provider.QuestionCacheProvider;
import qa.cache.like.remover.AnswerCacheRemover;
import qa.cache.like.remover.CommentAnswerCacheRemover;
import qa.cache.like.remover.CommentQuestionCacheRemover;
import qa.cache.like.remover.QuestionCacheRemover;
import qa.config.RedisConfiguration;
import qa.source.PasswordPropertyDataSource;

public class MockUtil { // TODO REFACTOR

    private static RedisConfiguration redisConfiguration;
    private static JedisResourceCenter jedisResourceCenter;

    private static QuestionCacheRemover questionCacheRemover;
    private static AnswerCacheRemover answerCacheRemover;
    private static CommentQuestionCacheRemover commentQuestionCacheRemover;
    private static CommentAnswerCacheRemover commentAnswerCacheRemover;

    private static QuestionCacheProvider questionCacheProvider;
    private static AnswerCacheProvider answerCacheProvider;
    private static CommentQuestionCacheProvider commentQuestionCacheProvider;
    private static CommentAnswerCacheProvider commentAnswerCacheProvider;

    private static QuestionLikesProvider questionLikesProvider;
    private static AnswerLikeProvider answerLikeProvider;
    private static CommentQuestionLikeProvider commentQuestionLikeProvider;
    private static CommentAnswerLikeProvider commentAnswerLikeProvider;

    private static final QuestionToLikeSetOperation questionLikeOperation;
    private static final AnswerToLikeSetOperation answerLikeOperation;
    private static final CommentQuestionToLikeSetOperation commentQuestionLikeOperation;
    private static final CommentAnswerToLikeSetOperation commentAnswerLikeOperation;
    private static final UserQuestionLikeSetOperation userQuestionLikeOperation;
    private static final UserAnswerLikeSetOperation userAnswerLikeOperation;
    private static final UserCommentQuestionLikeSetOperation userCommentQuestionLikeOperation;
    private static final UserCommentAnswerLikeSetOperation userCommentAnswerLikeOperation;

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

    public static QuestionLikesProvider mockQuestionLikeProvider() {
        if (questionLikesProvider == null) {
            questionLikesProvider = new QuestionLikesProvider(
                    userQuestionLikeOperation,
                    questionLikeOperation,
                    mockQuestionCacheProvider(),
                    mockQuestionCacheRemover()
            );
        }
        return questionLikesProvider;
    }

    public static AnswerLikeProvider mockAnswerLikeProvider() {
        if (answerLikeProvider == null) {
            answerLikeProvider = new AnswerLikeProvider(
                    userAnswerLikeOperation,
                    answerLikeOperation,
                    mockAnswerCacheProvider(),
                    mockAnswerCacheRemover()
            );
        }
        return answerLikeProvider;
    }

    public static CommentQuestionLikeProvider mockCommentQuestionLikeProvider() {
        if (commentQuestionLikeProvider == null) {
            commentQuestionLikeProvider = new CommentQuestionLikeProvider(
                    userCommentQuestionLikeOperation,
                    commentQuestionLikeOperation,
                    mockCommentQuestionCacheProvider(),
                    mockCommentQuestionCacheRemover()
            );
        }
        return commentQuestionLikeProvider;
    }

    public static CommentAnswerLikeProvider mockCommentAnswerLikeProvider() {
        if (commentAnswerLikeProvider == null) {
            commentAnswerLikeProvider = new CommentAnswerLikeProvider(
                    userCommentAnswerLikeOperation,
                    commentAnswerLikeOperation,
                    mockCommentAnswerCacheProvider(),
                    mockCommentAnswerCacheRemover()
            );
        }
        return commentAnswerLikeProvider;
    }

    private static QuestionCacheRemover mockQuestionCacheRemover() {
        if (questionCacheRemover == null) {
            questionCacheRemover = new QuestionCacheRemover(
                    userQuestionLikeOperation,
                    questionLikeOperation,
                    mockAnswerCacheRemover(),
                    mockCommentQuestionCacheRemover(),
                    mockCommentAnswerCacheRemover()
            );
        }
        return questionCacheRemover;
    }

    private static AnswerCacheRemover mockAnswerCacheRemover() {
        if (answerCacheRemover == null) {
            answerCacheRemover = new AnswerCacheRemover(
                    userAnswerLikeOperation,
                    answerLikeOperation,
                    mockCommentAnswerCacheRemover()
            );
        }
        return answerCacheRemover;
    }

    private static CommentQuestionCacheRemover mockCommentQuestionCacheRemover() {
        if (commentQuestionCacheRemover == null) {
            commentQuestionCacheRemover = new CommentQuestionCacheRemover(
                    userCommentQuestionLikeOperation,
                    commentQuestionLikeOperation
            );
        }
        return commentQuestionCacheRemover;
    }

    private static CommentAnswerCacheRemover mockCommentAnswerCacheRemover() {
        if (commentAnswerCacheRemover == null) {
            commentAnswerCacheRemover = new CommentAnswerCacheRemover(
                    userCommentAnswerLikeOperation,
                    commentAnswerLikeOperation
            );
        }
        return commentAnswerCacheRemover;
    }

    private static QuestionCacheProvider mockQuestionCacheProvider() {
        if (questionCacheProvider == null) {
            questionCacheProvider = new QuestionCacheProvider(
                    userQuestionLikeOperation,
                    questionLikeOperation,
                    mockAnswerCacheProvider(),
                    mockCommentQuestionCacheProvider(),
                    mockCommentAnswerCacheProvider()
            );
        }
        return questionCacheProvider;
    }

    private static AnswerCacheProvider mockAnswerCacheProvider() {
        if (answerCacheProvider == null) {
            answerCacheProvider = new AnswerCacheProvider(
                    userAnswerLikeOperation,
                    answerLikeOperation,
                    mockCommentAnswerCacheProvider()
            );
        }
        return answerCacheProvider;
    }

    private static CommentQuestionCacheProvider mockCommentQuestionCacheProvider() {
        if (commentQuestionCacheProvider == null) {
            commentQuestionCacheProvider = new CommentQuestionCacheProvider(
                    userCommentQuestionLikeOperation,
                    commentQuestionLikeOperation
            );
        }
        return commentQuestionCacheProvider;
    }

    private static CommentAnswerCacheProvider mockCommentAnswerCacheProvider() {
        if (commentAnswerCacheProvider == null) {
            commentAnswerCacheProvider = new CommentAnswerCacheProvider(
                    userCommentAnswerLikeOperation,
                    commentAnswerLikeOperation
            );
        }
        return commentAnswerCacheProvider;
    }

    private static PasswordPropertyDataSource mockPPDataSource() {
        PasswordPropertyDataSource propertyDataSource = Mockito.mock(PasswordPropertyDataSource.class);
        Mockito.lenient().when(propertyDataSource.getREDIS_PASSWORD_PATH()).thenReturn("/disk/main/forProjects/qa/redis/password.pass");
        return propertyDataSource;
    }
}
