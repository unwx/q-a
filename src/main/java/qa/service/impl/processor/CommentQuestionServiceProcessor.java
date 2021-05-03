package qa.service.impl.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import qa.dto.request.comment.*;
import qa.dto.response.comment.CommentQuestionResponse;
import qa.service.impl.processor.manager.CommentQuestionDataManager;
import qa.service.impl.processor.validator.CommentQuestionRequestValidator;
import qa.service.util.PrincipalUtil;

import java.util.List;

@Component
public class CommentQuestionServiceProcessor {

    private final CommentQuestionRequestValidator validator;
    private final CommentQuestionDataManager dataManager;

    @Autowired
    protected CommentQuestionServiceProcessor(CommentQuestionRequestValidator validator,
                                           CommentQuestionDataManager dataManager) {
        this.validator = validator;
        this.dataManager = dataManager;
    }

    protected long createCommentProcess(CommentQuestionCreateRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.throwBadRequestExIfQuestionNotExist(request.getQuestionId());
        return this.dataManager.saveNewComment(request, authentication);
    }

    protected void editCommentProcess(CommentQuestionEditRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.checkIsRealAuthorCommentQuestion(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getCommentId());
        this.dataManager.saveEditedComment(request);
    }

    protected void deleteCommentProcess(CommentQuestionDeleteRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.checkIsRealAuthorCommentQuestion(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getCommentId());
        this.dataManager.deleteCommentFromDatabase(request);
    }

    protected List<CommentQuestionResponse> getCommentProcess(Long questionId, Integer page, Authentication authentication) {
        return this.getCommentProcess(new CommentQuestionGetRequest(questionId, page), authentication);
    }

    protected List<CommentQuestionResponse> getCommentProcess(CommentQuestionGetRequest request, Authentication authentication) {
        this.validator.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        return this.dataManager.getCommentsResponse(request.getQuestionId(), userId, request.getPage());
    }

    protected void likeProcess(CommentQuestionLikeRequest request, Authentication authentication) {
        this.validator.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        this.dataManager.like(userId, request.getCommentId());
    }
}
