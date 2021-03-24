package qa.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.CommentDao;
import qa.dto.request.comment.*;
import qa.service.CommentService;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationChainAdditional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final ValidationPropertyDataSource validationPropertyDataSource;
    private final ValidationChainAdditional validationChain;

    public CommentServiceImpl(CommentDao commentDao,
                              ValidationPropertyDataSource validationPropertyDataSource,
                              ValidationChainAdditional validationChain) {
        this.commentDao = commentDao;
        this.validationPropertyDataSource = validationPropertyDataSource;
        this.validationChain = validationChain;
    }

    @Override
    public ResponseEntity<Long> createCommentQuestion(CommentQuestionCreateRequest request, Authentication authentication) {
        return null;
    }

    @Override
    public ResponseEntity<Long> createCommentAnswer(CommentAnswerCreateRequest request, Authentication authentication) {
        return null;
    }

    @Override
    public ResponseEntity<HttpStatus> editCommentQuestion(CommentQuestionEditRequest request, Authentication authentication) {
        return null;
    }

    @Override
    public ResponseEntity<HttpStatus> editCommentAnswer(CommentAnswerEditRequest request, Authentication authentication) {
        return null;
    }

    @Override
    public ResponseEntity<HttpStatus> deleteCommentQuestion(CommentQuestionDeleteRequest request, Authentication authentication) {
        return null;
    }

    @Override
    public ResponseEntity<HttpStatus> deleteCommentAnswer(CommentAnswerCreateRequest request, Authentication authentication) {
        return null;
    }
}
