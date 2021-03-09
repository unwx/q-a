package qa.dto.validation.wrapper;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.RegistrationRequestDto;
import qa.source.PropertiesDataSource;
import qa.validators.abstraction.ValidatorEntity;
import qa.validators.entities.ValidationNumberField;
import qa.validators.entities.ValidationObjectField;
import qa.validators.entities.ValidationStringField;

public class RegistrationRequestValidationWrapper extends RegistrationRequestDto implements ValidatorEntity {

    private final PropertiesDataSource propertiesDataSource;

    public RegistrationRequestValidationWrapper(RegistrationRequestDto request,
                                                PropertiesDataSource propertiesDataSource) {
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
                        getEmail(),
                        -1,
                        -1),
                new ValidationStringField(
                        getPassword(),
                        propertiesDataSource.getAUTHENTICATION_PASSWORD_LENGTH_MIN(),
                        propertiesDataSource.getUSER_USERNAME_LENGTH_MAX())
        };
    }

    @Override
    public @Nullable ValidationNumberField[] getNumberFields() {
        return null;
    }

    @Override
    public @Nullable ValidationObjectField[] getObjectFields() {
        return null;
    }
}
