package qa.validators.chain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationChain;
import qa.validators.abstraction.Validator;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.entities.ValidationNumberField;

public class NumberEntitiesValidator extends Validator implements ValidationChain {

    private final Logger logger = LogManager.getLogger(NumberEntitiesValidator.class);

    @Override
    public void validate(ValidationEntity entity) throws ValidationException {
        valuesValidate(entity);
    }

    private void valuesValidate(ValidationEntity entity) throws ValidationException {
        ValidationNumberField[] fields = entity.getNumberFields();
        for (ValidationNumberField f : fields) {
            if (f.getMax() != -1 && f.getNum() > f.getMax() || f.getMin() != -1 && f.getNum() < f.getMin()) {
                String message = formatMessage(
                        """
                        invalid value of: %s.\
                        (max value = %s\
                        min value = %s)\
                        """.formatted(f.getNum(), f.getMax(), f.getMin()));
                logger.info("[validation unsuccessful]: " + message);
                throw new ValidationException(message);
            }
        }
    }
}
