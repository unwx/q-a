package qa.dto.validation.wrapper.answer;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.answer.AnswerAnsweredRequest;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationNumberField;

public class AnswerAnsweredRequestValidationWrapper extends AnswerAnsweredRequest implements ValidationWrapper {

    public AnswerAnsweredRequestValidationWrapper(AnswerAnsweredRequest request) {
        super(request.getAnswerId());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getAnswerId(), -1L, 0L)
        };
    }
}
