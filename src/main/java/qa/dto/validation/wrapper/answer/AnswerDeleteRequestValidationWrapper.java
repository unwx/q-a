package qa.dto.validation.wrapper.answer;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.answer.AnswerDeleteRequest;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationNumberField;

public class AnswerDeleteRequestValidationWrapper extends AnswerDeleteRequest implements ValidationWrapper {

    public AnswerDeleteRequestValidationWrapper(AnswerDeleteRequest request) {
        super(request.getAnswerId());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getAnswerId(), -1L, 0L)
        };
    }
}
