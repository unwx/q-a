package qa.validators.additional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.Validator;

public class EmailValidator extends Validator implements AdditionalValidator<String> {

    private static final Logger logger = LogManager.getLogger(EmailValidator.class);

    @Override
    public void validate(String c) throws ValidationException {

        if (c == null) {
            String message = formatMessage("email = null");
            logger.info(unsuccessful + message);
            throw new ValidationException(message);
        }

        if (!org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(c)) {
            String message = formatMessage("invalid email by email pattern. (" + c + ")");
            logger.info(unsuccessful + message);
            throw new ValidationException(message);
        }
    }
}
