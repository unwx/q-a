package qa.dto.validation.wrapper.question;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.question.QuestionLikeRequest;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;

public class QuestionLikeRequestValidationWrapper extends QuestionLikeRequest implements ValidationWrapper {
    public QuestionLikeRequestValidationWrapper(QuestionLikeRequest request) {
        super(request.getQuestionId());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(super.questionId, -1L, 0L)
        };
    }
}
