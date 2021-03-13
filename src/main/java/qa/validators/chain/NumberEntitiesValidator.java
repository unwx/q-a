package qa.validators.chain;

import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationChain;
import qa.validators.abstraction.Validator;
import qa.validators.abstraction.ValidatorEntity;
import qa.validators.entities.ValidationNumberField;

public class NumberEntitiesValidator extends Validator implements ValidationChain {
    @Override
    public void validate(ValidatorEntity entity) throws ValidationException {
        valuesValidate(entity);
    }

    private void valuesValidate(ValidatorEntity entity) throws ValidationException {
        ValidationNumberField[] fields = entity.getNumberFields();
        for (ValidationNumberField f : fields) {
            if (f.getMax() != -1 && f.getNum() > f.getMax() || f.getMin() != -1 && f.getNum() < f.getMin())
                throw new ValidationException(formatMessage(
                        """
                        invalid value of: %s.\
                        (max value = %s\
                        min value = %s)\
                        """.formatted(f.getNum(), f.getMax(), f.getMin())
                ));
        }
    }
}
