package qa.validators;

import org.springframework.stereotype.Component;
import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationChainAdditional;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.entities.ValidationAdditional;

@Component
public class ValidationChainAdditionalImpl extends ValidationChainImpl implements ValidationChainAdditional {

    @Override
    public void validateWithAdditionalValidator(ValidationWrapper entity) throws ValidationException {
        validate(entity);
        additionalPart(entity);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void additionalPart(ValidationWrapper entity) throws ValidationException {
        if (entity.getAdditional() == null)
            return;
        for (ValidationAdditional validationAdditional : entity.getAdditional()) {
            if (validationAdditional != null)
                validationAdditional.getAdditionalValidator().validate(validationAdditional.getTarget());
        }
    }
}
