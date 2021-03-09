package qa.validators.additional;

import qa.exceptions.validator.ValidationException;
import qa.validators.additional.ValidationAdditional;
import qa.validators.abstraction.Validator;

public class EmailValidator extends Validator implements ValidationAdditional<String> {
    @Override
    public void validate(String c) throws ValidationException {
        if (!org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(c))
            throw new ValidationException("invalid email by email pattern.");
    }
}
