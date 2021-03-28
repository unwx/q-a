package qa.dto.validation.wrapper.user;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.user.UserGetQuestionsRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationNumberField;

public class UserGetQuestionsRequestValidationWrapper extends UserGetQuestionsRequest implements ValidationWrapper {

    private final ValidationPropertyDataSource propertyDataSource;

    public UserGetQuestionsRequestValidationWrapper(UserGetQuestionsRequest request,
                                                    ValidationPropertyDataSource propertyDataSource) {
        super(request.getId(), request.getPage());
        this.propertyDataSource = propertyDataSource;
    }

    public UserGetQuestionsRequestValidationWrapper(Long id, Integer page,
                                                    ValidationPropertyDataSource propertyDataSource) {
        super(id, page);
        this.propertyDataSource = propertyDataSource;
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[] {
                new ValidationNumberField(getPage(), -1, 1),
                new ValidationNumberField(getId(), -1L, 0L)
        };
    }
}
