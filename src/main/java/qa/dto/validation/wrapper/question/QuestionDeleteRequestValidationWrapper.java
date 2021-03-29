package qa.dto.validation.wrapper.question;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.question.QuestionDeleteRequest;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;

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
