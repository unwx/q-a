package qa.dto.validation.wrapper.comment;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.comment.CommentQuestionLikeRequest;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationNumberField;

public class CommentQuestionLikeRequestValidationWrapper extends CommentQuestionLikeRequest implements ValidationWrapper {
    public CommentQuestionLikeRequestValidationWrapper(CommentQuestionLikeRequest request) {
        super(request.getCommentId());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(super.commentId, -1L, 0L)
        };
    }
}
