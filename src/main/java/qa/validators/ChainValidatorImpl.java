package qa.validators;

import org.springframework.stereotype.Component;
import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationChainAdditional;
import qa.validators.abstraction.ValidatorEntity;
import qa.validators.additional.ValidationAdditional;
import qa.validators.chain.NullValidator;
import qa.validators.chain.NumberEntitiesValidator;
import qa.validators.chain.StringEntitiesValidator;
import qa.validators.entities.ValidationFieldType;

import java.util.List;

@Component
public class ChainValidatorImpl implements ValidationChainAdditional {

    private List<ValidationFieldType> ignore;
    private final NullValidator nullValidator = new NullValidator();
    private final StringEntitiesValidator stringEntitiesValidator = new StringEntitiesValidator();
    private final NumberEntitiesValidator numberEntitiesValidator = new NumberEntitiesValidator();

    public ChainValidatorImpl() {}

    @Override
    public void validate(ValidatorEntity entity) throws ValidationException {
        nullIgnorePart(entity);
        stringPart(entity);
        numberPart(entity);
    }

    @Override
    public <T> void validateWithAdditionalValidator(ValidatorEntity entity,
                                                    T entityAdditional,
                                                    ValidationAdditional<T> validationAdditional) throws ValidationException {
        validate(entity);
        validationAdditional.validate(entityAdditional);
    }

    private void nullIgnorePart(ValidatorEntity entity) throws ValidationException {
        nullValidator.validate(entity);
        ignore = nullValidator.getIgnore();
    }

    private void stringPart(ValidatorEntity entity) throws ValidationException {
        if (ignore.contains(ValidationFieldType.STRING))
            return;
        stringEntitiesValidator.validate(entity);
    }

    private void numberPart(ValidatorEntity entity) throws ValidationException {
        if (ignore.contains(ValidationFieldType.NUMBER))
            return;
        numberEntitiesValidator.validate(entity);
    }
}
