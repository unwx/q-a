package qa.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dto.request.comment.*;
import qa.dto.response.comment.CommentAnswerResponse;
import qa.service.CommentAnswerService;
import qa.service.impl.processor.CommentAnswerServiceProcessor;
import qa.service.impl.processor.manager.CommentAnswerDataManager;
import qa.service.impl.processor.validator.CommentAnswerRequestValidator;

import java.util.List;

@Service
public class CommentAnswerServiceImpl extends CommentAnswerServiceProcessor implements CommentAnswerService {

    public CommentAnswerServiceImpl(CommentAnswerRequestValidator validator,
                                    CommentAnswerDataManager dataManager) {
        super(validator, dataManager);
    }

    @Override
    public ResponseEntity<Long> createComment(CommentAnswerCreateRequest request, Authentication authentication) {
        final long id = super.createCommentProcess(request, authentication);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> editComment(CommentAnswerEditRequest request, Authentication authentication) {
        super.editCommentProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteComment(CommentAnswerDeleteRequest request, Authentication authentication) {
        super.deleteCommentProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CommentAnswerResponse>> getComments(Long answerId, Integer page, Authentication authentication) {
        final List<CommentAnswerResponse> response = super.getCommentsProcess(answerId, page, authentication);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CommentAnswerResponse>> getComments(CommentAnswerGetRequest request, Authentication authentication) {
        final List<CommentAnswerResponse> response = super.getCommentsProcess(request, authentication);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> like(CommentAnswerLikeRequest request, Authentication authentication) {
        super.likeProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
