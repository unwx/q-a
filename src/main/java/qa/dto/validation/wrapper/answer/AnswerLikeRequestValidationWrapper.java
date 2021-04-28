package qa.dto.validation.wrapper.answer;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.answer.AnswerLikeRequest;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;

public class AnswerLikeRequestValidationWrapper extends AnswerLikeRequest implements ValidationWrapper {
    public AnswerLikeRequestValidationWrapper(AnswerLikeRequest request) {
        super(request.getAnswerId());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(super.getAnswerId(), -1L, 0L)
        };
    }
}
