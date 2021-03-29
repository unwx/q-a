package qa.dto.validation.wrapper.user;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.user.UserGetAnswersRequest;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;

public class UserGetAnswersRequestValidationWrapper extends UserGetAnswersRequest implements ValidationWrapper {

    public UserGetAnswersRequestValidationWrapper(Long userId, Integer page) {
        super(userId, page);
    }

    public UserGetAnswersRequestValidationWrapper(UserGetAnswersRequest request) {
        super(request.getUserId(), request.getPage());
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getUserId(), -1L, 0L),
                new ValidationNumberField(getPage(), -1, 1)
        };
    }
}
