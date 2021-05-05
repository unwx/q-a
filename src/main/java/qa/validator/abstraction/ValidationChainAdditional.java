package qa.validator.abstraction;

import qa.exceptions.validator.ValidationException;

public interface ValidationChainAdditional extends ValidationChain {
    void validateWithAdditionalValidator(ValidationWrapper entity) throws ValidationException;
}
