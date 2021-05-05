package qa.validator.abstraction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.exceptions.validator.ValidationException;

public abstract class ValidatorLogger {

    private static final Logger logger = LogManager.getLogger(ValidatorLogger.class);

    public ValidationException logAndThrow(String message) {
        logger.trace(message);
        return new ValidationException(message);
    }
}
