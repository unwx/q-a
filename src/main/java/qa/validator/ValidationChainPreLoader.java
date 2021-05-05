package qa.validator;

import qa.validator.abstraction.Validator;
import qa.validator.chain.NullValidator;
import qa.validator.chain.NumberEntitiesValidator;
import qa.validator.chain.RegexValidator;
import qa.validator.chain.StringEntitiesValidator;

public class ValidationChainPreLoader {

    public static Validator[] load() {
        final int length = 4;
        final Validator[] validators = new Validator[length];

        final Validator nullValidator = new NullValidator();                    // - 0
        final Validator numberValidator = new NumberEntitiesValidator();        // - 1
        final Validator stringValidator = new StringEntitiesValidator();        // - 2
        final Validator regexValidator = new RegexValidator();                  // - 3

        validators[0] = nullValidator;
        validators[1] = numberValidator;
        validators[2] = stringValidator;
        validators[3] = regexValidator;

        return validators;
    }
}
