package qa.dto.validation.wrapper.question;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.question.QuestionEditRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.additional.TagsValidator;
import qa.validator.entities.ValidationAdditional;
import qa.validator.entities.ValidationNumberField;
import qa.validator.entities.ValidationStringField;

public class QuestionEditRequestValidationWrapper extends QuestionEditRequest implements ValidationWrapper {

    private final ValidationPropertyDataSource propertyDataSource;

    public QuestionEditRequestValidationWrapper(QuestionEditRequest request,
                                                ValidationPropertyDataSource propertyDataSource) {
        super(request.getQuestionId(), request.getText(), request.getTags());
        this.propertyDataSource = propertyDataSource;
    }

    @Override
    @Nullable
    public ValidationStringField[] getStringFields() {
        return new ValidationStringField[]{
                new ValidationStringField(
                        getText(),
                        propertyDataSource.getQuestion().getQUESTION_TEXT_LENGTH_MIN(),
                        propertyDataSource.getQuestion().getQUESTION_TEXT_LENGTH_MAX())
        };
    }

    @Override
    @Nullable
    public ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[]{
                new ValidationNumberField(getQuestionId(), -1L, 0L)
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
