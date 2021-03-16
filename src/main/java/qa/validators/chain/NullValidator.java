package qa.validators.chain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationChain;
import qa.validators.abstraction.ValidationNestedField;
import qa.validators.abstraction.Validator;
import qa.validators.abstraction.ValidatorEntity;
import qa.validators.entities.ValidationFieldType;
import qa.validators.entities.ValidationNumberField;
import qa.validators.entities.ValidationObjectField;
import qa.validators.entities.ValidationStringField;

import java.util.LinkedList;
import java.util.List;

/**
 * part of the validation chain.
 * <p></p>
 * if there are no required fields, <h3>return null!</h3>
 */
public class NullValidator extends Validator implements ValidationChain {

    private final List<ValidationFieldType> ignore = new LinkedList<>();

    private final Logger logger = LogManager.getLogger(NullValidator.class);

    @Override
    public void validate(ValidatorEntity entity) throws ValidationException {
        areAttributesAreNotNull(entity);
    }

    private void areAttributesAreNotNull(ValidatorEntity entity) throws ValidationException {
        objectPart(entity);
        stringPart(entity);
        numberPart(entity);
    }

    private void objectPart(ValidatorEntity entity) throws ValidationException {
        ValidationObjectField[] objectsFields = entity.getObjectFields();
        ignoreProcess(objectsFields, ValidationFieldType.OBJECT);
        nullValidationNestedProcess(objectsFields, ValidationFieldType.OBJECT);
    }

    private void stringPart(ValidatorEntity entity) throws ValidationException {
        ValidationStringField[] stringFields = entity.getStringFields();
        ignoreProcess(stringFields, ValidationFieldType.STRING);
        nullValidationNestedProcess(stringFields, ValidationFieldType.STRING);
    }

    private void numberPart(ValidatorEntity entity) throws ValidationException {
        ValidationNumberField[] numberFields = entity.getNumberFields();
        ignoreProcess(numberFields, ValidationFieldType.NUMBER);
        nullValidationNestedProcess(numberFields, ValidationFieldType.NUMBER);
    }


    private void ignoreProcess(Object[] objects, ValidationFieldType type) {
        if (objects == null)
            ignore.add(type);
    }

    private void nullValidationNestedProcess(ValidationNestedField[] fields, ValidationFieldType type) throws ValidationException {
        if (ignore.contains(type))
            return;

        for (ValidationNestedField f : fields) {
            for (Object o : f.getNested()) {
                if (o == null) {
                    String message = formatMessage("required field = null.");
                    logger.info("[validation unsuccessful]: " + message);
                    throw new ValidationException(message);
                }
            }
        }
    }

    public List<ValidationFieldType> getIgnore() {
        return ignore;
    }
}
