package qa.validator.chain;

import qa.exceptions.validator.ValidationException;
import qa.validator.abstraction.ValidationField;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.abstraction.Validator;
import qa.validator.entities.*;

import java.util.HashSet;

/**
 * part of the validation chain.
 * <p></p>
 * if there are no required fields, <h3>return null!</h3>
 */
public class NullValidator extends Validator {

    private static final String ERR_NULL_FIELD = "required field is null";

    @Override
    public void validate(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        this.areAttributesAreNotNull(entity, ignore);
    }

    private void areAttributesAreNotNull(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        this.objectPart(entity, ignore);
        this.stringPart(entity, ignore);
        this.numberPart(entity, ignore);
        this.regexPart(entity, ignore);
    }

    private void objectPart(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (!ignore.contains(ValidationIgnoreType.OBJECT)) {
            final ValidationObjectField[] objectsFields = entity.getObjectFields();
            this.nullValidationProcess(objectsFields);
        }
    }

    private void stringPart(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (!ignore.contains(ValidationIgnoreType.STRING)) {
            final ValidationStringField[] stringFields = entity.getStringFields();
            this.nullValidationProcess(stringFields);
        }
    }

    private void numberPart(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (!ignore.contains(ValidationIgnoreType.NUMBER)) {
            final ValidationNumberField[] numberFields = entity.getNumberFields();
            this.nullValidationProcess(numberFields);
        }
    }

    private void regexPart(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (!ignore.contains(ValidationIgnoreType.REGEX)) {
            final ValidationRegexField[] regexFields = entity.getRegexFields();
            this.nullValidationProcess(regexFields);
        }
    }

    private void nullValidationProcess(ValidationField[] fields) throws ValidationException {
        for (ValidationField f : fields) {
            for (Object o : f.getField()) {
                if (o == null) {
                    throw super.logAndThrow(ERR_NULL_FIELD);
                }
            }
        }
    }
}
