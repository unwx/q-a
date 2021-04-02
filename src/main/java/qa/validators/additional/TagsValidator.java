package qa.validators.additional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagsValidator extends Validator implements AdditionalValidator<String[]> {

    private final static Logger logger = LogManager.getLogger(TagsValidator.class);
    private final ValidationPropertyDataSource propertiesDataSource;

    public TagsValidator(ValidationPropertyDataSource propertiesDataSource) {
        this.propertiesDataSource = propertiesDataSource;
    }

    @Override
    public void validate(String[] c) throws ValidationException {
        if (c == null) {
            String message = formatMessage("tags must not be null");
            logger.info(unsuccessful + message);
            throw new ValidationException(message);
        }

        int counter = 0;
        Pattern pattern = Pattern.compile(propertiesDataSource.getQuestion().getQUESTION_TAG_REGEXP());
        for (String s : c) {
            if (s == null) {
                String message = formatMessage("tag must not be null");
                logger.info(unsuccessful + message);
                throw new ValidationException(message);
            }
            if (s.length() > propertiesDataSource.getQuestion().getQUESTION_TAG_LENGTH_MAX() ||
                s.length() < propertiesDataSource.getQuestion().getQUESTION_TAG_LENGTH_MIN()) {
                String message = formatMessage("tag length must be >= 2 and <= 20");
                logger.info(unsuccessful + message);
                throw new ValidationException(message);
            }
            Matcher matcher = pattern.matcher(s);
            if (!matcher.find()) {
                String message = formatMessage("the tag has unresolved characters");
                logger.info(unsuccessful + message);
                throw new ValidationException(message);
            }
            counter++;
        }

        if (counter > propertiesDataSource.getQuestion().getQUESTION_TAGS_COUNT_MAX() ||
            counter < propertiesDataSource.getQuestion().getQUESTION_TAGS_COUNT_MIN()) {
            String message = "tags count must be >= 1 and <= 7";
            logger.info(unsuccessful + message);
            throw new ValidationException(message);
        }
    }
}
