package qa.dto.validation.wrapper.answer;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.answer.AnswerGetFullRequest;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationNumberField;

public class AnswerGetFullRequestValidationWrapper extends AnswerGetFullRequest implements ValidationWrapper {

    public AnswerGetFullRequestValidationWrapper(AnswerGetFullRequest request) {
        super(request.getQuestionId(), request.getPage());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getQuestionId(), -1L, 0L),
                new ValidationNumberField(getPage(), 1, 1)
        };
    }
}
