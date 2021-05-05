package qa.validator.abstraction;

import qa.exceptions.validator.ValidationException;

public interface ValidationChain {
    void validate(ValidationWrapper entity) throws ValidationException;
}
