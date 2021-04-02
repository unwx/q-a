package qa.dto.validation.wrapper.question;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.question.QuestionGetCommentsRequest;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;

public class QuestionGetCommentsRequestValidationWrapper extends QuestionGetCommentsRequest implements ValidationWrapper {

    public QuestionGetCommentsRequestValidationWrapper(QuestionGetCommentsRequest request) {
        super(request.getQuestionId(), request.getPage());
    }

    public QuestionGetCommentsRequestValidationWrapper(Long questionId, Integer page) {
        super(questionId, page);
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getQuestionId(), -1L, 0L),
                new ValidationNumberField(getPage(), -1, 1)
        };
    }
}
