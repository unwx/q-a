package qa.validators.abstraction;

import qa.exceptions.validator.ValidationException;

public interface ValidationChain {
    void validate(ValidationEntity entity) throws ValidationException;
}
