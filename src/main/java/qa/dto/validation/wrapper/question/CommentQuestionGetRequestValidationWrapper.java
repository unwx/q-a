package qa.dto.validation.wrapper.question;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.comment.CommentQuestionGetRequest;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;

public class CommentQuestionGetRequestValidationWrapper extends CommentQuestionGetRequest implements ValidationWrapper {

    public CommentQuestionGetRequestValidationWrapper(CommentQuestionGetRequest request) {
        super(request.getQuestionId(), request.getPage());
    }

    public CommentQuestionGetRequestValidationWrapper(Long questionId, Integer page) {
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
