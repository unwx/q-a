package qa.dto.validation.wrapper;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.RegistrationRequestDto;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.additional.EmailValidator;
import qa.validators.entities.*;

public class RegistrationRequestValidationWrapper extends RegistrationRequestDto implements ValidationEntity {

    private final ValidationPropertyDataSource propertiesDataSource;

    public RegistrationRequestValidationWrapper(RegistrationRequestDto request,
                                                ValidationPropertyDataSource propertiesDataSource) {
        super(request.getUsername(), request.getEmail(), request.getPassword());
        this.propertiesDataSource = propertiesDataSource;
    }

    @Override
    public @Nullable ValidationStringField[] getStringFields() {
        return new ValidationStringField[]{
                new ValidationStringField(
                        getUsername(),
                        propertiesDataSource.getUSER_USERNAME_LENGTH_MIN(),
                        propertiesDataSource.getUSER_USERNAME_LENGTH_MAX()),
                new ValidationStringField(
                        getPassword(),
                        propertiesDataSource.getAUTHENTICATION_PASSWORD_LENGTH_MIN(),
                        propertiesDataSource.getUSER_USERNAME_LENGTH_MAX())
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
                new ValidationRegexField(propertiesDataSource.getUSER_USERNAME_REGEXP(), new String[]{getUsername()})
        };
    }
}
