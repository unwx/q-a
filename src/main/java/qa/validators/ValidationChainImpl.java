package qa.validators;

import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationChain;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.chain.*;
import qa.validators.entities.ValidationIgnoreType;

import java.util.HashSet;

public class ValidationChainImpl implements ValidationChain {

    private final NullValidator nullValidator = new NullValidator();
    private final StringEntitiesValidator stringEntitiesValidator = new StringEntitiesValidator();
    private final NumberEntitiesValidator numberEntitiesValidator = new NumberEntitiesValidator();
    private final RegexValidator regexValidator = new RegexValidator();
    private final IgnorePartValidator ignorePartValidator = new IgnorePartValidator();

    public ValidationChainImpl() {}

    @Override
    public void validate(ValidationEntity entity) throws ValidationException {
        HashSet<ValidationIgnoreType> ignore = ignorePart(entity);
        nullIgnorePart(entity, ignore);
        stringPart(entity, ignore);
        numberPart(entity, ignore);
        regexPart(entity, ignore);
    }

    private HashSet<ValidationIgnoreType> ignorePart(ValidationEntity entity) {
        return ignorePartValidator.getIgnore(entity);
    }

    private void nullIgnorePart(ValidationEntity entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        nullValidator.validate(entity, ignore);
    }

    private void stringPart(ValidationEntity entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (ignore.contains(ValidationIgnoreType.STRING))
            return;
        stringEntitiesValidator.validate(entity);
    }

    private void numberPart(ValidationEntity entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (ignore.contains(ValidationIgnoreType.NUMBER))
            return;
        numberEntitiesValidator.validate(entity);
    }

    private void regexPart(ValidationEntity entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (ignore.contains(ValidationIgnoreType.REGEX))
            return;
        regexValidator.validate(entity);
    }
}
