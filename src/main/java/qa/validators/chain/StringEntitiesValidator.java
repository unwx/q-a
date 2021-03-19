package qa.validators.chain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationChain;
import qa.validators.abstraction.Validator;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.entities.ValidationStringField;

public class StringEntitiesValidator extends Validator implements ValidationChain {

    private final Logger logger = LogManager.getLogger(StringEntitiesValidator.class);

    @Override
    public void validate(ValidationEntity entity) throws ValidationException {
        lengthValidate(entity);
    }

    private void lengthValidate(ValidationEntity entity) throws ValidationException {
        ValidationStringField[] fields = entity.getStringFields();
        for (ValidationStringField s : fields) {
            if (s.getMaxLen() != -1 && s.getS().length() > s.getMaxLen() || s.getMinLen() != -1 && s.getS().length() < s.getMinLen()) {
                String message = formatMessage(
                        """
                        invalid length of: %s.\
                        (max length = %s;\
                        min length = %s)\
                        """.formatted(s.getS(), s.getMaxLen(), s.getMinLen()));
                        logger.info("[validation unsuccessful]: " + message);
                throw new ValidationException(message);
            }
        }
    }
}
