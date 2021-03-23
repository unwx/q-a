package qa.dto.validation.wrapper.answer;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.answer.AnswerAnsweredRequest;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;

public class AnswerAnsweredRequestValidationWrapper extends AnswerAnsweredRequest implements ValidationWrapper {

    public AnswerAnsweredRequestValidationWrapper(AnswerAnsweredRequest request) {
        super(request.getId());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getId(), -1L, 0L)
        };
    }
}
