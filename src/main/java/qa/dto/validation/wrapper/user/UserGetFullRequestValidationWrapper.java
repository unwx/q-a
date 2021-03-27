package qa.dto.validation.wrapper.user;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.user.UserGetFullRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationStringField;

public class UserGetFullRequestValidationWrapper extends UserGetFullRequest implements ValidationWrapper {

    private final ValidationPropertyDataSource propertyDataSource;

    public UserGetFullRequestValidationWrapper(UserGetFullRequest request,
                                               ValidationPropertyDataSource propertyDataSource) {
        super(request.getUsername());
        this.propertyDataSource = propertyDataSource;
    }

    @Override
    public @Nullable ValidationStringField[] getStringFields() {
        return new ValidationStringField[] {
                new ValidationStringField(
                        getUsername(),
                        propertyDataSource.getUser().getUSER_USERNAME_LENGTH_MIN(),
                        propertyDataSource.getUser().getUSER_USERNAME_LENGTH_MAX())
        };
    }
}
