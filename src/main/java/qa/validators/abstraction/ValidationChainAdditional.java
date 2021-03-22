package qa.validators.abstraction;

import qa.exceptions.validator.ValidationException;

public interface ValidationChainAdditional extends ValidationChain {
    void validateWithAdditionalValidator(ValidationEntity entity) throws ValidationException;
}
