package qa.util;

import qa.exceptions.rest.BadRequestException;
import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationChain;
import qa.validators.abstraction.ValidationChainAdditional;
import qa.validators.abstraction.ValidationWrapper;

public final class ValidationUtil {

    private ValidationUtil() {}

    public static void validate(ValidationWrapper validationWrapper, ValidationChain validationChain) {
        try {
            validationChain.validate(validationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public static void validateWithAdditional(ValidationWrapper validationWrapper, ValidationChainAdditional validationChain) {
        try {
            validationChain.validateWithAdditionalValidator(validationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
