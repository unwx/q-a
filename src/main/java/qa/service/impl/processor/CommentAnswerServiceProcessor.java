package qa.service.impl.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import qa.dto.request.comment.*;
import qa.dto.response.comment.CommentAnswerResponse;
import qa.service.impl.processor.manager.CommentAnswerDataManager;
import qa.service.impl.processor.validator.CommentAnswerRequestValidator;
import qa.service.util.PrincipalUtil;

import java.util.List;

@Component
public class CommentAnswerServiceProcessor {

    private final CommentAnswerRequestValidator validator;
    private final CommentAnswerDataManager dataManager;

    @Autowired
    public CommentAnswerServiceProcessor(CommentAnswerRequestValidator validator,
                                         CommentAnswerDataManager dataManager) {
        this.validator = validator;
        this.dataManager = dataManager;
    }

    protected Long createCommentProcess(CommentAnswerCreateRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.throwBadRequestExIfAnswerNotExist(request.getAnswerId());
        return this.dataManager.saveNewComment(request, authentication);
    }

    protected void editCommentProcess(CommentAnswerEditRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.checkIsRealAuthor(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getCommentId());
        this.dataManager.saveEditedComment(request);
    }

    protected void deleteCommentProcess(CommentAnswerDeleteRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.checkIsRealAuthor(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getCommentId());
        this.dataManager.deleteCommentFromDatabase(request);
    }

    protected List<CommentAnswerResponse> getCommentsProcess(Long answerId, Integer page, Authentication authentication) {
        return this.getCommentsProcess(new CommentAnswerGetRequest(answerId, page), authentication);
    }

    protected List<CommentAnswerResponse> getCommentsProcess(CommentAnswerGetRequest request, Authentication authentication) {
        this.validator.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        return this.dataManager.getCommentsResponse(request.getAnswerId(), userId, request.getPage());
    }

    protected void likeProcess(CommentAnswerLikeRequest request, Authentication authentication) {
        this.validator.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        this.dataManager.like(request.getCommentId(), userId);
    }
}
