package qa.dto.validation.wrapper;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.AuthenticationRequestDto;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.additional.EmailValidator;
import qa.validators.entities.*;

public class AuthenticationRequestValidationWrapper extends AuthenticationRequestDto implements ValidationEntity {

    private final ValidationPropertyDataSource propertiesDataSource;
    public AuthenticationRequestValidationWrapper(AuthenticationRequestDto dto,
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
    public ValidationNumberField[] getNumberFields() {
        return null;
    }

    @Override
    @Nullable
    public ValidationObjectField[] getObjectFields() {
        return null;
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
    @Nullable
    public ValidationRegexField[] getRegexFields() {
        return null;
    }
}
