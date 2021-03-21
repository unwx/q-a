package qa.dto.validation.wrapper.authentication;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.additional.EmailValidator;
import qa.validators.entities.*;

public class AuthenticationRequestValidationWrapper extends AuthenticationRequest implements ValidationEntity {

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
                        propertiesDataSource.getAUTHENTICATION_PASSWORD_LENGTH_MIN(),
                        propertiesDataSource.getAUTHENTICATION_PASSWORD_LENGTH_MAX())
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
