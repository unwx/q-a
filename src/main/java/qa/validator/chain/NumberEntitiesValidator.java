package qa.validator.chain;

import qa.exceptions.validator.ValidationException;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.abstraction.Validator;
import qa.validator.entities.ValidationIgnoreType;
import qa.validator.entities.ValidationNumberField;

import java.util.HashSet;

public class NumberEntitiesValidator extends Validator {

    private static final String ERR_INVALID_VALUE =
                """
                invalid value of: %s.\s\
                (max value = %s\s\
                min value = %s)\
                """;

    @Override
    public void validate(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (!ignore.contains(ValidationIgnoreType.NUMBER))
            this.valuesValidate(entity);
    }

    private void valuesValidate(ValidationWrapper entity) throws ValidationException {
        final ValidationNumberField[] fields =  entity.getNumberFields();
        for (ValidationNumberField f : fields) {
            this.validate(f.getNum(), f.getMin(), f.getMax());
        }
    }

    private void validate(Number num, Number min, Number max) throws ValidationException {
        if (num instanceof Long) {
            if (isNotValid((Long) num, (Long) min,(Long) max))
                this.unsuccessfulValidationProcess(num, min, max);
            return;
        }
        if (num instanceof Integer) {
            if (isNotValid((Integer) num, (Integer) min,(Integer) max))
                this.unsuccessfulValidationProcess(num, min, max);
            return;
        }
        if (num instanceof Double) {
            if (isNotValid((Double) num, (Double) min,(Double) max))
                this.unsuccessfulValidationProcess(num, min, max);
            return;
        }
        if (num instanceof Float) {
            if (isNotValid((Float) num, (Float) min,(Float) max))
                this.unsuccessfulValidationProcess(num, min, max);
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
        final String message = ERR_INVALID_VALUE.formatted(num, max, min);
        throw super.logAndThrow(message);
    }
}
