package qa.service.impl.processor.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dto.request.question.*;
import qa.dto.validation.wrapper.question.*;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationChainAdditional;

@Component
public class QuestionRequestValidator extends RequestValidator {

    private final ValidationPropertyDataSource propertyDataSource;

    @Autowired
    public QuestionRequestValidator(ValidationPropertyDataSource propertyDataSource,
                                    ValidationChainAdditional validationChain) {
        super(validationChain);
        this.propertyDataSource = propertyDataSource;
    }

    public void validate(QuestionCreateRequest request) {
        super.validateWithAdditional(new QuestionCreateRequestValidationWrapper(request, propertyDataSource));
    }

    public void validate(QuestionEditRequest request) {
        super.validateWithAdditional(new QuestionEditRequestValidationWrapper(request, propertyDataSource));
    }

    public void validate(QuestionDeleteRequest request) {
        super.validate(new QuestionDeleteRequestValidationWrapper(request));
    }

    public void validate(QuestionGetViewsRequest request) {
        super.validate(new QuestionGetViewsRequestValidationWrapper(request));
    }

    public void validate(QuestionGetFullRequest request) {
        super.validate(new QuestionGetFullRequestValidationWrapper(request));
    }

    public void validate(QuestionLikeRequest request) {
        super.validate(new QuestionLikeRequestValidationWrapper(request));
    }
}
