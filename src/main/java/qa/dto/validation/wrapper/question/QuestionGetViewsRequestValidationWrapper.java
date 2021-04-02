package qa.dto.validation.wrapper.question;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.question.QuestionGetViewsRequest;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;

public class QuestionGetViewsRequestValidationWrapper extends QuestionGetViewsRequest implements ValidationWrapper {

    public QuestionGetViewsRequestValidationWrapper(QuestionGetViewsRequest request) {
        super(request.getPage());
    }

    public QuestionGetViewsRequestValidationWrapper(Integer page) {
        super(page);
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getPage(), -1, 1)
        };
    }
}
