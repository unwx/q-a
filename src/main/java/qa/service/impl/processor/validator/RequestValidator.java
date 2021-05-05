package qa.service.impl.processor.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.exceptions.rest.BadRequestException;
import qa.exceptions.validator.ValidationException;
import qa.validator.abstraction.ValidationChainAdditional;
import qa.validator.abstraction.ValidationWrapper;

@Component
public abstract class RequestValidator {

    private final ValidationChainAdditional validationChain;

    @Autowired
    public RequestValidator(ValidationChainAdditional validationChain) {
        this.validationChain = validationChain;
    }

    /**
     *
     * @throws BadRequestException:
     * if the request is not valid
     */
    public void validate(ValidationWrapper validationWrapper) {
        try { this.validationChain.validate(validationWrapper); }
        catch (ValidationException e) { throw new BadRequestException(e.getMessage()); }
    }

    /**
     *
     * @throws BadRequestException:
     * if the request is not valid
     */
    public void validateWithAdditional(ValidationWrapper validationWrapper) {
        try { this.validationChain.validateWithAdditionalValidator(validationWrapper); }
        catch (ValidationException e) { throw new BadRequestException(e.getMessage()); }
    }
}
