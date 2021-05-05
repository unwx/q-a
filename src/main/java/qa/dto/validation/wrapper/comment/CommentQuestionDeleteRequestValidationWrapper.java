package qa.dto.validation.wrapper.comment;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.comment.CommentQuestionDeleteRequest;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationNumberField;

public class CommentQuestionDeleteRequestValidationWrapper extends CommentQuestionDeleteRequest implements ValidationWrapper {

    public CommentQuestionDeleteRequestValidationWrapper(CommentQuestionDeleteRequest request) {
        super(request.getCommentId());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getCommentId(), -1L, 0L)
        };
    }
}
