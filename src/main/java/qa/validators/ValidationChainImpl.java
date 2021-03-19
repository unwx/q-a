package qa.validators;

import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationChain;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.chain.NullValidator;
import qa.validators.chain.NumberEntitiesValidator;
import qa.validators.chain.StringEntitiesValidator;
import qa.validators.entities.ValidationFieldType;

import java.util.List;

public class ValidationChainImpl implements ValidationChain {

    private List<ValidationFieldType> ignore;
    private final NullValidator nullValidator = new NullValidator();
    private final StringEntitiesValidator stringEntitiesValidator = new StringEntitiesValidator();
    private final NumberEntitiesValidator numberEntitiesValidator = new NumberEntitiesValidator();

    public ValidationChainImpl() {}

    @Override
    public void validate(ValidationEntity entity) throws ValidationException {
        nullIgnorePart(entity);
        stringPart(entity);
        numberPart(entity);
    }

    private void nullIgnorePart(ValidationEntity entity) throws ValidationException {
        nullValidator.validate(entity);
        ignore = nullValidator.getIgnore();
    }

    private void stringPart(ValidationEntity entity) throws ValidationException {
        if (ignore.contains(ValidationFieldType.STRING))
            return;
        stringEntitiesValidator.validate(entity);
    }

    private void numberPart(ValidationEntity entity) throws ValidationException {
        if (ignore.contains(ValidationFieldType.NUMBER))
            return;
        numberEntitiesValidator.validate(entity);
    }
}
