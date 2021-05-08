package qa.validator;

import org.springframework.stereotype.Component;
import qa.exceptions.validator.ValidationException;
import qa.validator.abstraction.ValidationChainAdditional;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.additional.AdditionalValidator;
import qa.validator.entities.ValidationAdditional;

@Component
public class ValidationChainAdditionalImpl extends ValidationChainImpl implements ValidationChainAdditional {

    @Override
    public void validateWithAdditionalValidator(ValidationWrapper entity) throws ValidationException {
        super.validate(entity);
        this.additionalPart(entity);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void additionalPart(ValidationWrapper entity) throws ValidationException {
        if (entity.getAdditional() == null)
            return;
        for (ValidationAdditional validationAdditional : entity.getAdditional()) {
            if (validationAdditional != null) {
                final AdditionalValidator<Object> validator = validationAdditional.getAdditionalValidator();
                final Object target = validationAdditional.getTarget();

                validator.validate(target);
            }
        }
    }
}
