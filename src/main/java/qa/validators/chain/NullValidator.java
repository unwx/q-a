package qa.validators.chain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationChain;
import qa.validators.abstraction.ValidationField;
import qa.validators.abstraction.Validator;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.entities.*;

import java.util.LinkedList;
import java.util.List;

/**
 * part of the validation chain.
 * <p></p>
 * if there are no required fields, <h3>return null!</h3>
 */
public class NullValidator extends Validator implements ValidationChain {

    private final List<ValidationIgnoreType> ignore = new LinkedList<>();

    private final Logger logger = LogManager.getLogger(NullValidator.class);

    @Override
    public void validate(ValidationEntity entity) throws ValidationException {
        areAttributesAreNotNull(entity);
    }

    private void areAttributesAreNotNull(ValidationEntity entity) throws ValidationException {
        objectPart(entity);
        stringPart(entity);
        numberPart(entity);
        regexPart(entity);
    }

    private void objectPart(ValidationEntity entity) throws ValidationException {
        ValidationObjectField[] objectsFields = entity.getObjectFields();
        ignoreProcess(objectsFields, ValidationIgnoreType.OBJECT);
        nullValidationProcess(objectsFields, ValidationIgnoreType.OBJECT);
    }

    private void stringPart(ValidationEntity entity) throws ValidationException {
        ValidationStringField[] stringFields = entity.getStringFields();
        ignoreProcess(stringFields, ValidationIgnoreType.STRING);
        nullValidationProcess(stringFields, ValidationIgnoreType.STRING);
    }

    private void numberPart(ValidationEntity entity) throws ValidationException {
        ValidationNumberField[] numberFields = entity.getNumberFields();
        ignoreProcess(numberFields, ValidationIgnoreType.NUMBER);
        nullValidationProcess(numberFields, ValidationIgnoreType.NUMBER);
    }

    private void regexPart(ValidationEntity entity) throws ValidationException {
        ValidationRegexField[] regexFields = entity.getRegexFields();
        ignoreProcess(regexFields, ValidationIgnoreType.REGEX);
        nullValidationProcess(regexFields, ValidationIgnoreType.REGEX);
    }


    private void ignoreProcess(Object[] objects, ValidationIgnoreType type) {
        if (objects == null)
            ignore.add(type);
    }

    private void nullValidationProcess(ValidationField[] fields, ValidationIgnoreType type) throws ValidationException {
        if (ignore.contains(type))
            return;

        for (ValidationField f : fields) {
            for (Object o : f.getField()) {
                if (o == null) {
                    String message = formatMessage("required field = null.");
                    logger.info("[validation unsuccessful]: " + message);
                    throw new ValidationException(message);
                }
            }
        }
    }

    public List<ValidationIgnoreType> getIgnore() {
        return ignore;
    }
}
