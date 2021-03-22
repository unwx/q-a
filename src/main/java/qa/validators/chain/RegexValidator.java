package qa.validators.chain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationWrapper;
import qa.validators.abstraction.Validator;
import qa.validators.entities.ValidationRegexField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator extends Validator {

    private final static Logger logger = LogManager.getLogger(RegexValidator.class);

    public void validate(ValidationWrapper entity) throws ValidationException {
        regexValidate(entity);
    }

    private void regexValidate(ValidationWrapper entity) throws ValidationException {
        ValidationRegexField[] regexFields = entity.getRegexFields();
        for (ValidationRegexField f : regexFields) {
            Pattern pattern = Pattern.compile(f.getRegex());
            for (String s : f.getTargets()) {
                Matcher matcher = pattern.matcher(s);
                if (!matcher.find()) {
                    String message = formatMessage(
                            """
                            the entered string does not match the regular expression it needs.\s\
                            string: %s\s\
                            regex: %s\
                            """.formatted(s, f.getRegex())
                    );
                    logger.info("[validation unsuccessful]: " + message);
                    throw new ValidationException(message);
                }
            }
        }
    }
}
