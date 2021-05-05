package qa.dto.validation.wrapper.authentication;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.authentication.RegistrationRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.additional.EmailValidator;
import qa.validator.entities.ValidationAdditional;
import qa.validator.entities.ValidationRegexField;
import qa.validator.entities.ValidationStringField;

public class RegistrationRequestValidationWrapper extends RegistrationRequest implements ValidationWrapper {

    private final ValidationPropertyDataSource propertiesDataSource;

    public RegistrationRequestValidationWrapper(RegistrationRequest request,
                                                ValidationPropertyDataSource propertiesDataSource) {
        super(request.getUsername(), request.getEmail(), request.getPassword());
        this.propertiesDataSource = propertiesDataSource;
    }

    @Override
    public @Nullable ValidationStringField[] getStringFields() {
        return new ValidationStringField[]{
                new ValidationStringField(
                        getUsername(),
                        propertiesDataSource.getUser().getUSER_USERNAME_LENGTH_MIN(),
                        propertiesDataSource.getUser().getUSER_USERNAME_LENGTH_MAX()),
                new ValidationStringField(
                        getPassword(),
                        propertiesDataSource.getAuthentication().getAUTHENTICATION_PASSWORD_LENGTH_MIN(),
                        propertiesDataSource.getUser().getUSER_USERNAME_LENGTH_MAX())
        };
    }

    @Override
    @Nullable
    @SuppressWarnings("rawtypes")
    public ValidationAdditional[] getAdditional() {
        return new ValidationAdditional[]{
                new ValidationAdditional<>(getEmail(), new EmailValidator()),
        };
    }

    @Override
    public @Nullable ValidationRegexField[] getRegexFields() {
        return new ValidationRegexField[]{
                new ValidationRegexField(propertiesDataSource.getUser().getUSER_USERNAME_REGEXP(), new String[]{getUsername()})
        };
    }
}
