package qa.dto.validation.wrapper.question;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.question.QuestionDeleteRequest;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.entities.ValidationNumberField;

public class QuestionDeleteRequestValidationWrapper extends QuestionDeleteRequest implements ValidationEntity {
    public QuestionDeleteRequestValidationWrapper(QuestionDeleteRequest request) {
        super(request.getId());
    }

    @Override
    @Nullable
    public ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[]{
                new ValidationNumberField(getId(), -1L, 0L)
        };
    }
}
