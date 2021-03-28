package qa.validators.chain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.abstraction.Validator;
import qa.validators.entities.ValidationNumberField;

public class NumberEntitiesValidator extends Validator {

    private final static Logger logger = LogManager.getLogger(NumberEntitiesValidator.class);

    public void validate(ValidationWrapper entity) throws ValidationException {
        valuesValidate(entity);
    }

    private void valuesValidate(ValidationWrapper entity) throws ValidationException {
        ValidationNumberField[] fields =  entity.getNumberFields();
        for (ValidationNumberField f : fields) {
            validate(f.getNum(), f.getMin(), f.getMax());
        }
    }

    private void validate(Number num, Number min, Number max) throws ValidationException {
        if (num instanceof Long) {
            if (isNotValid((Long) num, (Long) min,(Long) max))
                unsuccessfulValidationProcess(num, min, max);
            return;
        }
        if (num instanceof Integer) {
            if (isNotValid((Integer) num, (Integer) min,(Integer) max))
                unsuccessfulValidationProcess(num, min, max);
            return;
        }
        if (num instanceof Double) {
            if (isNotValid((Double) num, (Double) min,(Double) max))
                unsuccessfulValidationProcess(num, min, max);
            return;
        }
        if (num instanceof Float) {
            if (isNotValid((Float) num, (Float) min,(Float) max))
                unsuccessfulValidationProcess(num, min, max);
        }
    }

    private boolean isNotValid(Double num, Double min, Double max) {
        return max != -1 && num > max || min != -1 && num < min;
    }

    private boolean isNotValid(Float num, Float min, Float max) {
        return max != -1 && num > max || min != -1 && num < min;
    }

    private boolean isNotValid(Integer num, Integer min, Integer max) {
        return max != -1 && num > max || min != -1 && num < min;
    }

    private boolean isNotValid(Long num, Long min, Long max) {
        return max != -1 && num > max || min != -1 && num < min;
    }

    private void unsuccessfulValidationProcess(Object num, Object min, Object max) throws ValidationException {
        String message = formatMessage(
                """
                invalid value of: %s.\s\
                (max value = %s\s\
                min value = %s)\
                """.formatted(num, max, min));
        logger.info("[validation unsuccessful]: " + message);
        throw new ValidationException(message);
    }
}
