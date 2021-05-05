package qa.validator.chain;

import qa.exceptions.validator.ValidationException;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.abstraction.Validator;
import qa.validator.entities.ValidationIgnoreType;
import qa.validator.entities.ValidationStringField;

import java.util.HashSet;

public class StringEntitiesValidator extends Validator {

    private static final String ERR_INVALID_LENGTH =
            """
            invalid length of: %s.\s\
            (max length = %s;\s\
            min length = %s)\
            """;

    @Override
    public void validate(ValidationWrapper entity, HashSet<ValidationIgnoreType> ignore) throws ValidationException {
        if (!ignore.contains(ValidationIgnoreType.STRING))
            this.lengthValidate(entity);
    }

    private void lengthValidate(ValidationWrapper entity) throws ValidationException {
        final ValidationStringField[] fields = entity.getStringFields();

        for (ValidationStringField s : fields) {
            if (s.getMaxLen() != -1 && s.getS().length() > s.getMaxLen() || s.getMinLen() != -1 && s.getS().length() < s.getMinLen()) {
                final String message = ERR_INVALID_LENGTH.formatted(s.getS(), s.getMaxLen(), s.getMinLen());
                throw super.logAndThrow(message);
            }
        }
    }
}