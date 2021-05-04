package qa.service.impl.processor.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dto.request.comment.*;
import qa.dto.validation.wrapper.answer.CommentAnswerGetRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentAnswerCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentAnswerDeleteRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentAnswerEditRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentAnswerLikeRequestValidationWrapper;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationChainAdditional;

@Component
public class CommentAnswerRequestValidator extends RequestValidator {

    private final ValidationPropertyDataSource propertyDataSource;

    @Autowired
    public CommentAnswerRequestValidator(ValidationChainAdditional validationChain,
                                         ValidationPropertyDataSource propertyDataSource) {
        super(validationChain);
        this.propertyDataSource = propertyDataSource;
    }

    public void validate(CommentAnswerCreateRequest request) {
        super.validate(new CommentAnswerCreateRequestValidationWrapper(request, propertyDataSource));
    }

    public void validate(CommentAnswerEditRequest request) {
        super.validate(new CommentAnswerEditRequestValidationWrapper(request, propertyDataSource));
    }

    public void validate(CommentAnswerDeleteRequest request) {
        super.validate(new CommentAnswerDeleteRequestValidationWrapper(request));
    }

    public void validate(CommentAnswerGetRequest request) {
        super.validate(new CommentAnswerGetRequestValidationWrapper(request));
    }

    public void validate(CommentAnswerLikeRequest request) {
        super.validate(new CommentAnswerLikeRequestValidationWrapper(request));
    }
}
