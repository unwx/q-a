package qa.service.impl.aid.process.validation;

import org.springframework.stereotype.Component;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.validation.wrapper.authentication.AuthenticationRequestValidationWrapper;
import qa.dto.validation.wrapper.authentication.RegistrationRequestValidationWrapper;
import qa.service.util.ValidationUtil;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationChainAdditional;

@Component
public class AuthenticationServiceValidation {

    private final ValidationPropertyDataSource propertyDataSource;
    private final ValidationChainAdditional chainValidator;

    public AuthenticationServiceValidation(ValidationPropertyDataSource propertyDataSource,
                                           ValidationChainAdditional chainValidator) {
        this.propertyDataSource = propertyDataSource;
        this.chainValidator = chainValidator;
    }

    public void validate(AuthenticationRequest request) {
        ValidationUtil.validateWithAdditional(new AuthenticationRequestValidationWrapper(request, propertyDataSource), chainValidator);
    }

    public void validate(RegistrationRequest request) {
        ValidationUtil.validateWithAdditional(new RegistrationRequestValidationWrapper(request, propertyDataSource), chainValidator);
    }
}
