package qa.dto.validation.wrapper.authentication;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.additional.EmailValidator;
import qa.validator.entities.ValidationAdditional;
import qa.validator.entities.ValidationStringField;

public class AuthenticationRequestValidationWrapper extends AuthenticationRequest implements ValidationWrapper {

    private final ValidationPropertyDataSource propertiesDataSource;
    public AuthenticationRequestValidationWrapper(AuthenticationRequest dto,
                                                  ValidationPropertyDataSource propertiesDataSource) {
        super(dto.getEmail(), dto.getPassword());
        this.propertiesDataSource = propertiesDataSource;
    }

    @Override
    @Nullable
    public ValidationStringField[] getStringFields() {
        return new ValidationStringField[]{
                new ValidationStringField(
                        getPassword(),
                        propertiesDataSource.getAuthentication().getAUTHENTICATION_PASSWORD_LENGTH_MIN(),
                        propertiesDataSource.getAuthentication().getAUTHENTICATION_PASSWORD_LENGTH_MAX())
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
}
