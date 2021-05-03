package qa.service.impl.aid.process.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dto.request.answer.*;
import qa.dto.validation.wrapper.answer.*;
import qa.service.util.ValidationUtil;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationChainAdditional;

@Component
public class AnswerServiceValidation {

    private final ValidationPropertyDataSource propertyDataSource;
    private final ValidationChainAdditional validationChain;

    @Autowired
    public AnswerServiceValidation(ValidationPropertyDataSource propertyDataSource,
                                   ValidationChainAdditional validationChain) {
        this.propertyDataSource = propertyDataSource;
        this.validationChain = validationChain;
    }

    public void validate(AnswerCreateRequest request) {
        ValidationUtil.validate(new AnswerCreateRequestValidationWrapper(request, propertyDataSource), validationChain);
    }

    public void validate(AnswerEditRequest request) {
        ValidationUtil.validate(new AnswerEditRequestValidationWrapper(request, propertyDataSource), validationChain);
    }

    public void validate(AnswerAnsweredRequest request) {
        ValidationUtil.validate(new AnswerAnsweredRequestValidationWrapper(request), validationChain);
    }

    public void validate(AnswerDeleteRequest request) {
        ValidationUtil.validate(new AnswerDeleteRequestValidationWrapper(request), validationChain);
    }

    public void validate(AnswerGetFullRequest request) {
        ValidationUtil.validate(new AnswerGetFullRequestValidationWrapper(request), validationChain);
    }

    public void validate(AnswerLikeRequest request) {
        ValidationUtil.validate(new AnswerLikeRequestValidationWrapper(request), validationChain);
    }
}
