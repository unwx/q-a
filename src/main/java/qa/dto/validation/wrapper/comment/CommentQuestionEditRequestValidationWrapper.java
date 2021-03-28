package qa.dto.validation.wrapper.comment;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.comment.CommentQuestionEditRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;
import qa.validators.entities.ValidationStringField;

public class CommentQuestionEditRequestValidationWrapper extends CommentQuestionEditRequest implements ValidationWrapper {

    private final ValidationPropertyDataSource propertyDataSource;

    public CommentQuestionEditRequestValidationWrapper(CommentQuestionEditRequest request,
                                                       ValidationPropertyDataSource propertyDataSource) {
        super(request.getId(), request.getText());
        this.propertyDataSource = propertyDataSource;
    }

    @Override
    @Nullable
    public ValidationStringField[] getStringFields() {
        return new ValidationStringField[] {
                new ValidationStringField(
                        getText(),
                        propertyDataSource.getComment().getCOMMENT_TEXT_LENGTH_MIN(),
                        propertyDataSource.getComment().getCOMMENT_TEXT_LENGTH_MAX())
        };
    }

    @Override
    @Nullable
    public ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getId(), -1L, 0L)
        };
    }
}
