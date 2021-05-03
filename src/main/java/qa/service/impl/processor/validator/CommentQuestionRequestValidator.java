package qa.service.impl.processor.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dto.request.comment.*;
import qa.dto.validation.wrapper.comment.CommentQuestionCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentQuestionDeleteRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentQuestionEditRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentQuestionLikeRequestValidationWrapper;
import qa.dto.validation.wrapper.question.CommentQuestionGetRequestValidationWrapper;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationChainAdditional;

@Component
public class CommentQuestionRequestValidator extends RequestValidator {

    private final ValidationPropertyDataSource propertyDataSource;

    @Autowired
    public CommentQuestionRequestValidator(ValidationChainAdditional validationChain,
                                           ValidationPropertyDataSource propertyDataSource) {
        super(validationChain);
        this.propertyDataSource = propertyDataSource;
    }

    public void validate(CommentQuestionCreateRequest request) {
        super.validate(new CommentQuestionCreateRequestValidationWrapper(request, propertyDataSource));
    }

    public void validate(CommentQuestionEditRequest request) {
        super.validate(new CommentQuestionEditRequestValidationWrapper(request, propertyDataSource));
    }

    public void validate(CommentQuestionDeleteRequest request) {
        super.validate(new CommentQuestionDeleteRequestValidationWrapper(request));
    }

    public void validate(CommentQuestionGetRequest request) {
        super.validate(new CommentQuestionGetRequestValidationWrapper(request));
    }

    public void validate(CommentQuestionLikeRequest request) {
        super.validate(new CommentQuestionLikeRequestValidationWrapper(request));
    }
}
