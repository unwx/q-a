package qa.validator.chain;

import qa.exceptions.validator.ValidationException;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.abstraction.Validator;
import qa.validator.entities.ValidationIgnoreType;
import qa.validator.entities.ValidationRegexField;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator extends Validator {

    private static final String ERR_NOT_MATCH =
            """
            the entered string does not match the regular expression it needs.\s\
            string: %s\s\
            regex: %s\
            """;

    @Override
    public void validate(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (!ignore.contains(ValidationIgnoreType.REGEX))
            this.regexValidate(entity);
    }

    private void regexValidate(ValidationWrapper entity) throws ValidationException {
        final ValidationRegexField[] regexFields = entity.getRegexFields();
        for (ValidationRegexField f : regexFields) {

            final Pattern pattern = Pattern.compile(f.getRegex());
            for (String s : f.getTargets()) {

                final Matcher matcher = pattern.matcher(s);
                if (!matcher.find()) {
                    final String message = ERR_NOT_MATCH.formatted(s, f.getRegex());
                    throw super.logAndThrow(message);
                }
            }
        }
    }
}
