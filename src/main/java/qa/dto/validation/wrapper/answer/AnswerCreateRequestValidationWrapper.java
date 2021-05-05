package qa.dto.validation.wrapper.answer;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.answer.AnswerCreateRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationNumberField;
import qa.validator.entities.ValidationStringField;

public class AnswerCreateRequestValidationWrapper extends AnswerCreateRequest implements ValidationWrapper {

    private final ValidationPropertyDataSource propertyDataSource;

    public AnswerCreateRequestValidationWrapper(AnswerCreateRequest request,
                                                ValidationPropertyDataSource propertyDataSource) {
        super(request.getQuestionId(), request.getText());
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
                        getQuestionId(), -1L, 0L)
        };
    }
}
