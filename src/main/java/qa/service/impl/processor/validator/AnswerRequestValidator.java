package qa.service.impl.processor.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dto.request.answer.*;
import qa.dto.validation.wrapper.answer.*;
import qa.source.ValidationPropertyDataSource;
import qa.validator.abstraction.ValidationChainAdditional;

@Component
public class AnswerRequestValidator extends RequestValidator {

    private final ValidationPropertyDataSource propertyDataSource;

    @Autowired
    public AnswerRequestValidator(ValidationPropertyDataSource propertyDataSource,
                                  ValidationChainAdditional validationChain) {
        super(validationChain);
        this.propertyDataSource = propertyDataSource;
    }

    public void validate(AnswerCreateRequest request) {
        super.validate(new AnswerCreateRequestValidationWrapper(request, propertyDataSource));
    }

    public void validate(AnswerEditRequest request) {
        super.validate(new AnswerEditRequestValidationWrapper(request, propertyDataSource));
    }

    public void validate(AnswerAnsweredRequest request) {
        super.validate(new AnswerAnsweredRequestValidationWrapper(request));
    }

    public void validate(AnswerDeleteRequest request) {
        super.validate(new AnswerDeleteRequestValidationWrapper(request));
    }

    public void validate(AnswerGetFullRequest request) {
        super.validate(new AnswerGetFullRequestValidationWrapper(request));
    }

    public void validate(AnswerLikeRequest request) {
        super.validate(new AnswerLikeRequestValidationWrapper(request));
    }
}
