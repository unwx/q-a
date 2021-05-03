package qa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dto.request.comment.*;
import qa.dto.response.comment.CommentQuestionResponse;
import qa.service.CommentQuestionService;
import qa.service.impl.processor.CommentQuestionServiceProcessor;
import qa.service.impl.processor.manager.CommentQuestionDataManager;
import qa.service.impl.processor.validator.CommentQuestionRequestValidator;

import java.util.List;

@Service
public class CommentQuestionServiceImpl extends CommentQuestionServiceProcessor implements CommentQuestionService {

    @Autowired
    protected CommentQuestionServiceImpl(CommentQuestionRequestValidator validator,
                                         CommentQuestionDataManager dataManager) {
        super(validator, dataManager);
    }

    @Override
    public ResponseEntity<Long> createComment(CommentQuestionCreateRequest request, Authentication authentication) {
        final long id = super.createCommentProcess(request, authentication);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> editComment(CommentQuestionEditRequest request, Authentication authentication) {
        super.editCommentProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteComment(CommentQuestionDeleteRequest request, Authentication authentication) {
        super.deleteCommentProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CommentQuestionResponse>> getComments(Long questionId, Integer page, Authentication authentication) {
        final List<CommentQuestionResponse> response = super.getCommentProcess(questionId, page, authentication);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CommentQuestionResponse>> getComments(CommentQuestionGetRequest request, Authentication authentication) {
        final List<CommentQuestionResponse> response = super.getCommentProcess(request, authentication);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> like(CommentQuestionLikeRequest request, Authentication authentication) {
        super.likeProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
