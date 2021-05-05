package qa.validator.abstraction;

import qa.exceptions.validator.ValidationException;
import qa.validator.entities.ValidationIgnoreType;

import java.util.HashSet;

public abstract class Validator extends ValidatorLogger {
    public abstract void validate(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException;
}
