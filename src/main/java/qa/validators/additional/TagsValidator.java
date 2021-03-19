package qa.validators.additional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.Validator;

public class TagsValidator extends Validator implements AdditionalValidator<String[]> {

    private final Logger logger = LogManager.getLogger(TagsValidator.class);
    private final ValidationPropertyDataSource propertiesDataSource;

    public TagsValidator(ValidationPropertyDataSource propertiesDataSource) {
        this.propertiesDataSource = propertiesDataSource;
    }

    @Override
    public void validate(String[] c) throws ValidationException {
        if (c == null) {
            String message = "tags must not be null";
            logger.info("[validation unsuccessful]: " + message);
            throw new ValidationException(message);
        }

        int counter = 0;
        for (String s : c) {
            if (s == null) {
                String message = "tag must not be null";
                logger.info("[validation unsuccessful]: " + message);
                throw new ValidationException(message);
            }
            if (s.length() > propertiesDataSource.getQUESTION_TAG_LENGTH_MAX() ||
                s.length() < propertiesDataSource.getQUESTION_TAG_LENGTH_MIN()) {
                String message = "tag length must be >= 2 and <= 20";
                logger.info("[validation unsuccessful]: " + message);
                throw new ValidationException(message);
            }
            counter++;
        }

        if (counter > propertiesDataSource.getQUESTION_TAGS_COUNT_MAX() ||
            counter < propertiesDataSource.getQUESTION_TAGS_COUNT_MIN()) {
            String message = "tags count must be >= 1 and <= 7";
            logger.info("[validation unsuccessful]: " + message);
            throw new ValidationException(message);
        }
    }
}
