package qa.dto.validation.wrapper.question;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.question.QuestionGetViewsRequest;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationNumberField;

public class QuestionGetViewsRequestValidationWrapper extends QuestionGetViewsRequest implements ValidationWrapper {

    public QuestionGetViewsRequestValidationWrapper(QuestionGetViewsRequest request) {
        super(request.getPage());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getPage(), -1, 1)
        };
    }
}
