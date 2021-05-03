package qa.service.impl.processor.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dto.request.user.UserGetAnswersRequest;
import qa.dto.request.user.UserGetFullRequest;
import qa.dto.request.user.UserGetQuestionsRequest;
import qa.dto.validation.wrapper.user.UserGetAnswersRequestValidationWrapper;
import qa.dto.validation.wrapper.user.UserGetFullRequestValidationWrapper;
import qa.dto.validation.wrapper.user.UserGetQuestionsRequestValidationWrapper;
import qa.service.util.ValidationUtil;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationChainAdditional;

@Component
public class UserRequestValidator {

    private final ValidationPropertyDataSource propertyDataSource;
    private final ValidationChainAdditional validationChain;

    @Autowired
    public UserRequestValidator(ValidationPropertyDataSource propertyDataSource,
                                ValidationChainAdditional validationChain) {
        this.propertyDataSource = propertyDataSource;
        this.validationChain = validationChain;
    }

    public void validate(UserGetFullRequest request) {
        ValidationUtil.validate(new UserGetFullRequestValidationWrapper(request, propertyDataSource), validationChain);
    }

    public void validate(UserGetQuestionsRequest request) {
        ValidationUtil.validate(new UserGetQuestionsRequestValidationWrapper(request), validationChain);
    }

    public void validate(UserGetAnswersRequest request) {
        ValidationUtil.validate(new UserGetAnswersRequestValidationWrapper(request), validationChain);
    }
}
