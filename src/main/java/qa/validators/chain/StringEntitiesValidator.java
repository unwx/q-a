package qa.validators.chain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.abstraction.Validator;
import qa.validators.entities.ValidationStringField;

public class StringEntitiesValidator extends Validator {

    private final static Logger logger = LogManager.getLogger(StringEntitiesValidator.class);

    public void validate(ValidationWrapper entity) throws ValidationException {
        lengthValidate(entity);
    }

    private void lengthValidate(ValidationWrapper entity) throws ValidationException {
        ValidationStringField[] fields = entity.getStringFields();
        for (ValidationStringField s : fields) {
            if (s.getMaxLen() != -1 && s.getS().length() > s.getMaxLen() || s.getMinLen() != -1 && s.getS().length() < s.getMinLen()) {
                String message = formatMessage(
                        """
                        invalid length of: %s.\s\
                        (max length = %s;\s\
                        min length = %s)\
                        """.formatted(s.getS(), s.getMaxLen(), s.getMinLen()));
                        logger.info("[validation unsuccessful]: " + message);
                throw new ValidationException(message);
            }
        }
    }
}
