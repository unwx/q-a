package qa.dto.validation.wrapper;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.AuthenticationRequestDto;
import qa.source.PropertiesDataSource;
import qa.validators.abstraction.ValidatorEntity;
import qa.validators.entities.ValidationNumberField;
import qa.validators.entities.ValidationObjectField;
import qa.validators.entities.ValidationStringField;

public class AuthenticationRequestValidationWrapper extends AuthenticationRequestDto implements ValidatorEntity {

    private final PropertiesDataSource propertiesDataSource;
    public AuthenticationRequestValidationWrapper(AuthenticationRequestDto dto,
                                                  PropertiesDataSource propertiesDataSource) {
        super(dto.getEmail(), dto.getPassword());
        this.propertiesDataSource = propertiesDataSource;
    }

    @Override
    @Nullable
    public ValidationStringField[] getStringFields() {
        return new ValidationStringField[]{
                new ValidationStringField(
                        getEmail(),
                        -1,
                        -1),
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
}
