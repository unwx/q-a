package qa.service.impl.processor.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dto.request.user.UserGetAnswersRequest;
import qa.dto.request.user.UserGetFullRequest;
import qa.dto.request.user.UserGetQuestionsRequest;
import qa.dto.validation.wrapper.user.UserGetAnswersRequestValidationWrapper;
import qa.dto.validation.wrapper.user.UserGetFullRequestValidationWrapper;
import qa.dto.validation.wrapper.user.UserGetQuestionsRequestValidationWrapper;
import qa.source.ValidationPropertyDataSource;
import qa.validator.abstraction.ValidationChainAdditional;

@Component
public class UserRequestValidator extends RequestValidator {

    private final ValidationPropertyDataSource propertyDataSource;

    @Autowired
    public UserRequestValidator(ValidationPropertyDataSource propertyDataSource,
                                ValidationChainAdditional validationChain) {
        super(validationChain);
        this.propertyDataSource = propertyDataSource;
    }

    public void validate(UserGetFullRequest request) {
        super.validate(new UserGetFullRequestValidationWrapper(request, propertyDataSource));
    }

    public void validate(UserGetQuestionsRequest request) {
        super.validate(new UserGetQuestionsRequestValidationWrapper(request));
    }

    public void validate(UserGetAnswersRequest request) {
        super.validate(new UserGetAnswersRequestValidationWrapper(request));
    }
}
