package qa.validators.additional;

import qa.exceptions.validator.ValidationException;

public interface AdditionalValidator<T> {
    void validate(T c) throws ValidationException;
}
