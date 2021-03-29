package qa.dto.validation.wrapper.answer;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.answer.AnswerEditRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;
import qa.validators.entities.ValidationStringField;

public class AnswerEditRequestValidationWrapper extends AnswerEditRequest implements ValidationWrapper {

    private final ValidationPropertyDataSource propertyDataSource;

    public AnswerEditRequestValidationWrapper(AnswerEditRequest request,
                                              ValidationPropertyDataSource propertyDataSource) {
        super(request.getAnswerId(), request.getText());
        this.propertyDataSource = propertyDataSource;
    }

    @Override
    public @Nullable ValidationStringField[] getStringFields() {
        return new ValidationStringField[]{
                new ValidationStringField(
                        getText(),
                        propertyDataSource.getAnswer().getANSWER_TEXT_LENGTH_MIN(),
                        propertyDataSource.getAnswer().getANSWER_TEXT_LENGTH_MAX())
        };
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getAnswerId(), -1L, 0L)
        };
    }
}
