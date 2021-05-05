package qa.dto.validation.wrapper.answer;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.comment.CommentAnswerGetRequest;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationNumberField;

public class CommentAnswerGetRequestValidationWrapper extends CommentAnswerGetRequest implements ValidationWrapper {

    public CommentAnswerGetRequestValidationWrapper(Long answerId, Integer page) {
        super(answerId, page);
    }

    public CommentAnswerGetRequestValidationWrapper(CommentAnswerGetRequest request) {
        super(request.getAnswerId(), request.getPage());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getAnswerId(), -1L, 0L),
                new ValidationNumberField(getPage(), -1, 1)
        };
    }
}
