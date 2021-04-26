package qa.util.mock;

import org.mockito.Mockito;
import qa.cache.CacheRemover;
import qa.cache.operation.impl.*;

public class CacheRemoverMockTestUtil {

    private static CacheRemover cacheRemover;

    private CacheRemoverMockTestUtil() {}

    public static CacheRemover mock() {
        if (cacheRemover == null) {
            final QuestionToLikeSetOperation            questionLikeOperation               = Mockito.spy(QuestionToLikeSetOperation.class);
            final AnswerToLikeSetOperation              answerLikeOperation                 = Mockito.spy(AnswerToLikeSetOperation.class);
            final CommentQuestionToLikeSetOperation     commentQuestionLikeOperation        = Mockito.spy(CommentQuestionToLikeSetOperation.class);
            final CommentAnswerToLikeSetOperation       commentAnswerLikeOperation          = Mockito.spy(CommentAnswerToLikeSetOperation.class);
            final UserQuestionLikeSetOperation          userQuestionLikeOperation           = Mockito.spy(UserQuestionLikeSetOperation.class);
            final UserAnswerLikeSetOperation            userAnswerLikeOperation             = Mockito.spy(UserAnswerLikeSetOperation.class);
            final UserCommentQuestionLikeSetOperation   userCommentQuestionLikeOperation    = Mockito.spy(UserCommentQuestionLikeSetOperation.class);
            final UserCommentAnswerLikeSetOperation     userCommentAnswerLikeOperation      = Mockito.spy(UserCommentAnswerLikeSetOperation.class);
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
}
