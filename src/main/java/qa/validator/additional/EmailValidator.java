package qa.validator.additional;

import qa.exceptions.validator.ValidationException;

public class EmailValidator extends AdditionalValidator<String> {

    private static final String ERR_EMAIL_NULL          = "email is null";
    private static final String ERR_INVALID_PATTERN     = "invalid email: %s";

    @Override
    public void validate(String c) throws ValidationException {

        if (c == null)
            throw super.logAndThrow(ERR_EMAIL_NULL);

        if (!org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(c)) {
            final String message = ERR_INVALID_PATTERN.formatted(c);
            throw super.logAndThrow(message);
        }
    }
}
