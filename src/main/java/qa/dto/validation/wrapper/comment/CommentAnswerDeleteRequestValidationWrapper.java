package qa.dto.validation.wrapper.comment;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.comment.CommentAnswerDeleteRequest;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;

public class CommentAnswerDeleteRequestValidationWrapper extends CommentAnswerDeleteRequest implements ValidationWrapper {

    public CommentAnswerDeleteRequestValidationWrapper(CommentAnswerDeleteRequest request) {
        super(request.getId());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getId(), -1L, 0L)
        };
    }
}
