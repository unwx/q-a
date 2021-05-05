package qa.dto.validation.wrapper.question;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.question.QuestionDeleteRequest;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationNumberField;

public class QuestionDeleteRequestValidationWrapper extends QuestionDeleteRequest implements ValidationWrapper {
    public QuestionDeleteRequestValidationWrapper(QuestionDeleteRequest request) {
        super(request.getQuestionId());
    }

    @Override
    @Nullable
    public ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[]{
                new ValidationNumberField(getQuestionId(), -1L, 0L)
        };
    }
}
