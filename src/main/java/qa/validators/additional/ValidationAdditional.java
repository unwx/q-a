package qa.validators.additional;

import qa.exceptions.validator.ValidationException;

public interface ValidationAdditional<Consumes> {
    void validate(Consumes c) throws ValidationException;
}
