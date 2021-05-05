package qa.dto.validation.wrapper.comment;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.comment.CommentAnswerEditRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationNumberField;
import qa.validator.entities.ValidationStringField;

public class CommentAnswerEditRequestValidationWrapper extends CommentAnswerEditRequest implements ValidationWrapper {

    private final ValidationPropertyDataSource propertyDataSource;

    public CommentAnswerEditRequestValidationWrapper(CommentAnswerEditRequest request,
                                                     ValidationPropertyDataSource propertyDataSource) {
        super(request.getCommentId(), request.getText());
        this.propertyDataSource = propertyDataSource;
    }

    @Override
    public @Nullable ValidationStringField[] getStringFields() {
        return new ValidationStringField[] {
                new ValidationStringField(
                        getText(),
                        propertyDataSource.getComment().getCOMMENT_TEXT_LENGTH_MIN(),
                        propertyDataSource.getComment().getCOMMENT_TEXT_LENGTH_MAX())
        };
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getCommentId(), -1L, 0L)
        };
    }
}
