package qa.service.impl.processor.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dto.request.question.*;
import qa.dto.validation.wrapper.question.*;
import qa.service.util.ValidationUtil;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationChainAdditional;

@Component
public class QuestionRequestValidator {

    private final ValidationPropertyDataSource propertyDataSource;
    private final ValidationChainAdditional validationChain;

    @Autowired
    public QuestionRequestValidator(ValidationPropertyDataSource propertyDataSource,
                                    ValidationChainAdditional validationChain) {
        this.propertyDataSource = propertyDataSource;
        this.validationChain = validationChain;
    }

    public void validate(QuestionCreateRequest request) {
        ValidationUtil.validateWithAdditional(new QuestionCreateRequestValidationWrapper(request, propertyDataSource), validationChain);
    }

    public void validate(QuestionEditRequest request) {
        ValidationUtil.validateWithAdditional(new QuestionEditRequestValidationWrapper(request, propertyDataSource), validationChain);
    }

    public void validate(QuestionDeleteRequest request) {
        ValidationUtil.validate(new QuestionDeleteRequestValidationWrapper(request), validationChain);
    }

    public void validate(QuestionGetViewsRequest request) {
        ValidationUtil.validate(new QuestionGetViewsRequestValidationWrapper(request), validationChain);
    }

    public void validate(QuestionGetFullRequest request) {
        ValidationUtil.validate(new QuestionGetFullRequestValidationWrapper(request), validationChain);
    }

    public void validate(QuestionLikeRequest request) {
        ValidationUtil.validate(new QuestionLikeRequestValidationWrapper(request), validationChain);
    }
}
