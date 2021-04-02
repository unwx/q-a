package qa.validators.chain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationField;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.abstraction.Validator;
import qa.validators.entities.*;

import java.util.HashSet;

/**
 * part of the validation chain.
 * <p></p>
 * if there are no required fields, <h3>return null!</h3>
 */
public class NullValidator extends Validator {

    private final static Logger logger = LogManager.getLogger(NullValidator.class);

    public void validate(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        areAttributesAreNotNull(entity, ignore);
    }

    private void areAttributesAreNotNull(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        objectPart(entity, ignore);
        stringPart(entity, ignore);
        numberPart(entity, ignore);
        regexPart(entity, ignore);
    }

    private void objectPart(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (!ignore.contains(ValidationIgnoreType.OBJECT)) {
            ValidationObjectField[] objectsFields = entity.getObjectFields();
            nullValidationProcess(objectsFields);
        }
    }

    private void stringPart(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (!ignore.contains(ValidationIgnoreType.STRING)) {
            ValidationStringField[] stringFields = entity.getStringFields();
            nullValidationProcess(stringFields);
        }
    }

    private void numberPart(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (!ignore.contains(ValidationIgnoreType.NUMBER)) {
            ValidationNumberField[] numberFields = entity.getNumberFields();
            nullValidationProcess(numberFields);
        }
    }

    private void regexPart(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (!ignore.contains(ValidationIgnoreType.REGEX)) {
            ValidationRegexField[] regexFields = entity.getRegexFields();
            nullValidationProcess(regexFields);
        }
    }

    private void nullValidationProcess(ValidationField[] fields) throws ValidationException {
        for (ValidationField f : fields) {
            for (Object o : f.getField()) {
                if (o == null) {
                    String message = formatMessage("required field = null.");
                    logger.info(unsuccessful + message);
                    throw new ValidationException(message);
                }
            }
        }
    }
}
