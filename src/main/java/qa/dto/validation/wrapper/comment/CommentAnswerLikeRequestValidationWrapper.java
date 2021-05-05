package qa.dto.validation.wrapper.comment;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.comment.CommentAnswerLikeRequest;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationNumberField;

public class CommentAnswerLikeRequestValidationWrapper extends CommentAnswerLikeRequest implements ValidationWrapper {
    public CommentAnswerLikeRequestValidationWrapper(CommentAnswerLikeRequest request) {
        super(request.getCommentId());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(super.commentId, -1L, 0L)
        };
    }
}
