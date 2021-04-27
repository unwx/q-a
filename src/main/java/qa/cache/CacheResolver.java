package qa.cache;

import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.impl.*;
import qa.domain.DomainName;

public abstract class CacheResolver {

    protected final QuestionToLikeSetOperation questionLikeOperation;
    protected final AnswerToLikeSetOperation answerLikeOperation;
    protected final CommentQuestionToLikeSetOperation commentQuestionLikeOperation;
    protected final CommentAnswerToLikeSetOperation commentAnswerLikeOperation;

    protected final UserQuestionLikeSetOperation userQuestionLikeOperation;
    protected final UserAnswerLikeSetOperation userAnswerLikeOperation;
    protected final UserCommentQuestionLikeSetOperation userCommentQuestionLikeOperation;
    protected final UserCommentAnswerLikeSetOperation userCommentAnswerLikeOperation;

    private static final String ERR_UNKNOWN_ARGUMENT = "unknown argument: %s";

    protected CacheResolver(QuestionToLikeSetOperation questionLikeOperation,
                            AnswerToLikeSetOperation answerLikeOperation,
                            CommentQuestionToLikeSetOperation commentQuestionLikeOperation,
                            CommentAnswerToLikeSetOperation commentAnswerLikeOperation,
                            UserQuestionLikeSetOperation userQuestionLikeOperation,
                            UserAnswerLikeSetOperation userAnswerLikeOperation,
                            UserCommentQuestionLikeSetOperation userCommentQuestionLikeOperation,
                            UserCommentAnswerLikeSetOperation userCommentAnswerLikeOperation) {
        this.questionLikeOperation = questionLikeOperation;
        this.answerLikeOperation = answerLikeOperation;
        this.commentQuestionLikeOperation = commentQuestionLikeOperation;
        this.commentAnswerLikeOperation = commentAnswerLikeOperation;
        this.userQuestionLikeOperation = userQuestionLikeOperation;
        this.userAnswerLikeOperation = userAnswerLikeOperation;
        this.userCommentQuestionLikeOperation = userCommentQuestionLikeOperation;
        this.userCommentAnswerLikeOperation = userCommentAnswerLikeOperation;
    }

    protected CacheLikeOperation resolve(DomainName name) {
        return switch (name) {
            case QUESTION -> new CacheLikeOperation(
                    this.questionLikeOperation,
                    this.userQuestionLikeOperation
            );
            case ANSWER -> new CacheLikeOperation(
                    this.answerLikeOperation,
                    this.userAnswerLikeOperation

            );
            case COMMENT_QUESTION -> new CacheLikeOperation(
                    this.commentQuestionLikeOperation,
                    this.userCommentQuestionLikeOperation
            );
            case COMMENT_ANSWER -> new CacheLikeOperation(
                    this.commentAnswerLikeOperation,
                    this.userCommentAnswerLikeOperation
            );
            default -> throw new IllegalArgumentException(
                    ERR_UNKNOWN_ARGUMENT.formatted(name)
            );
        };
    }

    protected EntityToLikeSetOperation resolveEntityToLike(DomainName name) {
        return switch (name) {
            case QUESTION -> this.questionLikeOperation;

            case ANSWER -> this.answerLikeOperation;

            case COMMENT_QUESTION -> this.commentQuestionLikeOperation;

            case COMMENT_ANSWER -> this.commentAnswerLikeOperation;

            default -> throw new IllegalArgumentException(
                    ERR_UNKNOWN_ARGUMENT.formatted(name)
            );
        };
    }
}
