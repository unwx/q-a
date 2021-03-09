package qa.validators.abstraction;

import qa.exceptions.validator.ValidationException;
import qa.validators.additional.ValidationAdditional;

public interface ValidationChainAdditional extends ValidationChain {
    <T> void validateWithAdditionalValidator(ValidatorEntity entity, T additional, ValidationAdditional<T> validationAdditional) throws ValidationException;
}
