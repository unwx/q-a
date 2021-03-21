package qa.dto.validation.wrapper.question;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.question.QuestionEditRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.additional.TagsValidator;
import qa.validators.entities.*;

public class QuestionEditRequestValidationWrapper extends QuestionEditRequest implements ValidationEntity {

    private final ValidationPropertyDataSource propertyDataSource;

    public QuestionEditRequestValidationWrapper(QuestionEditRequest request,
                                                ValidationPropertyDataSource propertyDataSource) {
        super(request.getId(), request.getText(), request.getTags());
        this.propertyDataSource = propertyDataSource;
    }

    @Override
    @Nullable
    public ValidationStringField[] getStringFields() {
        return new ValidationStringField[]{
                new ValidationStringField(
                        getText(),
                        propertyDataSource.getQUESTION_TEXT_LENGTH_MIN(),
                        propertyDataSource.getQUESTION_TEXT_LENGTH_MAX())
        };
    }

    @Override
    @Nullable
    public ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[]{
                new ValidationNumberField(getId(), -1L, 0L)
        };
    }

    @Override
    @Nullable
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ValidationAdditional[] getAdditional() {
        return new ValidationAdditional[]{
                new ValidationAdditional(getTags(), new TagsValidator(propertyDataSource))
        };
    }
}
