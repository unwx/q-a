package qa.validator.additional;

import qa.exceptions.validator.ValidationException;
import qa.validator.abstraction.ValidatorLogger;

public abstract class AdditionalValidator<T> extends ValidatorLogger {
    public abstract void validate(T c) throws ValidationException;
}
