package qa.dto.validation.wrapper.answer;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.answer.AnswerCreateRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.entities.ValidationNumberField;
import qa.validators.entities.ValidationStringField;

public class AnswerCreateRequestValidationWrapper extends AnswerCreateRequest implements ValidationEntity {

    private final ValidationPropertyDataSource propertyDataSource;

    public AnswerCreateRequestValidationWrapper(AnswerCreateRequest request,
                                                ValidationPropertyDataSource propertyDataSource) {
        super(request.getId(), request.getText());
        this.propertyDataSource = propertyDataSource;
    }

    @Override
    public @Nullable ValidationStringField[] getStringFields() {
        return new ValidationStringField[] {
                new ValidationStringField(
                        getText(),
                        propertyDataSource.getAnswer().getANSWER_TEXT_LENGTH_MIN(),
                        propertyDataSource.getAnswer().getANSWER_TEXT_LENGTH_MAX())
        };
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(
                        getId(), -1L, 0L)
        };
    }
}
