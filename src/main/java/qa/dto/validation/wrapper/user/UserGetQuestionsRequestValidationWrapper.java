package qa.dto.validation.wrapper.user;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.user.UserGetQuestionsRequest;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;

public class UserGetQuestionsRequestValidationWrapper extends UserGetQuestionsRequest implements ValidationWrapper {

    public UserGetQuestionsRequestValidationWrapper(UserGetQuestionsRequest request) {
        super(request.getUserId(), request.getPage());
    }

    public UserGetQuestionsRequestValidationWrapper(Long id, Integer page) {
        super(id, page);
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getPage(), -1, 1),
                new ValidationNumberField(getUserId(), -1L, 0L)
        };
    }
}
