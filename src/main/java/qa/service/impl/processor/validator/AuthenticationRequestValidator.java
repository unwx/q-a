package qa.service.impl.processor.validator;

import org.springframework.stereotype.Component;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.validation.wrapper.authentication.AuthenticationRequestValidationWrapper;
import qa.dto.validation.wrapper.authentication.RegistrationRequestValidationWrapper;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationChainAdditional;

@Component
public class AuthenticationRequestValidator extends RequestValidator {

    private final ValidationPropertyDataSource propertyDataSource;

    public AuthenticationRequestValidator(ValidationPropertyDataSource propertyDataSource,
                                          ValidationChainAdditional validationChain) {
        super(validationChain);
        this.propertyDataSource = propertyDataSource;
    }

    public void validate(AuthenticationRequest request) {
        super.validateWithAdditional(new AuthenticationRequestValidationWrapper(request, propertyDataSource));
    }

    public void validate(RegistrationRequest request) {
        super.validateWithAdditional(new RegistrationRequestValidationWrapper(request, propertyDataSource));
    }
}
