package qa.validator;

import qa.exceptions.validator.ValidationException;
import qa.validator.abstraction.ValidationChain;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.abstraction.Validator;
import qa.validator.chain.IgnoreFieldExtractor;
import qa.validator.entities.ValidationIgnoreType;

import java.util.HashSet;

public class ValidationChainImpl implements ValidationChain {

    private final Validator[] validators = ValidationChainPreLoader.load();
    private final IgnoreFieldExtractor ignoreExtractor = new IgnoreFieldExtractor();

    @Override
    public void validate(ValidationWrapper entity) throws ValidationException {
        final HashSet<ValidationIgnoreType> ignore = ignoreExtractor.getIgnore(entity);

        for (Validator validator : validators) {
            validator.validate(entity, ignore);
        }
    }
}
