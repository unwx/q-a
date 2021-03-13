package qa.validators.chain;

import qa.exceptions.validator.ValidationException;
import qa.validators.abstraction.ValidationChain;
import qa.validators.abstraction.Validator;
import qa.validators.abstraction.ValidatorEntity;
import qa.validators.entities.ValidationStringField;

public class StringEntitiesValidator extends Validator implements ValidationChain {

    @Override
    public void validate(ValidatorEntity entity) throws ValidationException {
        lengthValidate(entity);
    }

    private void lengthValidate(ValidatorEntity entity) throws ValidationException {
        ValidationStringField[] fields = entity.getStringFields();
        for (ValidationStringField s : fields) {
            if (s.getMaxLen() != -1 && s.getS().length() > s.getMaxLen() || s.getMinLen() != -1 && s.getS().length() < s.getMinLen())
                throw new ValidationException(formatMessage(
                        """
                        invalid length of: %s.\
                        (max length = %s;\
                        min length = %s)\
                        """.formatted(s.getS(), s.getMaxLen(), s.getMinLen())
                ));
        }
    }
}
