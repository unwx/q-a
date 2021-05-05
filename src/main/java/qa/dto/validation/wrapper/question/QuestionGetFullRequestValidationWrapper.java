package qa.dto.validation.wrapper.question;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.question.QuestionGetFullRequest;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationNumberField;

public class QuestionGetFullRequestValidationWrapper extends QuestionGetFullRequest implements ValidationWrapper {

    public QuestionGetFullRequestValidationWrapper(QuestionGetFullRequest request) {
        super(request.getQuestionId());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getQuestionId(), -1L, 0L)
        };
    }
}
