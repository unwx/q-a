package qa.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.AnswerDao;
import qa.dao.CommentAnswerDao;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.request.comment.CommentAnswerCreateRequest;
import qa.dto.request.comment.CommentAnswerDeleteRequest;
import qa.dto.request.comment.CommentAnswerEditRequest;
import qa.dto.request.comment.CommentAnswerGetRequest;
import qa.dto.response.comment.CommentAnswerResponse;
import qa.dto.validation.wrapper.comment.CommentAnswerCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentAnswerDeleteRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentAnswerEditRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.service.CommentAnswerService;
import qa.source.ValidationPropertyDataSource;
import qa.util.ValidationUtil;
import qa.util.user.AuthorUtil;
import qa.util.user.PrincipalUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.List;

@Service
public class CommentAnswerServiceImpl implements CommentAnswerService {

    private final CommentAnswerDao commentAnswerDao;
    private final ValidationPropertyDataSource validationPropertyDataSource;
    private final ValidationChainAdditional validationChain;
    private final PropertySetterFactory propertySetterFactory;
    private final AnswerDao answerDao;

    private static final String ERR_MESSAGE_ANSWER_NOT_EXIST_ID = "answer not exist. answer id: %s";

    private static final Logger logger = LogManager.getLogger(CommentAnswerServiceImpl.class);

    @Autowired
    public CommentAnswerServiceImpl(CommentAnswerDao commentAnswerDao,
                                    ValidationPropertyDataSource validationPropertyDataSource,
                                    ValidationChainAdditional validationChain,
                                    PropertySetterFactory propertySetterFactory,
                                    AnswerDao answerDao) {
        this.commentAnswerDao = commentAnswerDao;
        this.validationPropertyDataSource = validationPropertyDataSource;
        this.validationChain = validationChain;
        this.propertySetterFactory = propertySetterFactory;
        this.answerDao = answerDao;
    }

    @Override
    public ResponseEntity<Long> createComment(CommentAnswerCreateRequest request, Authentication authentication) {
        return new ResponseEntity<>(createCommentProcess(request, authentication), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> editComment(CommentAnswerEditRequest request, Authentication authentication) {
        editCommentProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteComment(CommentAnswerDeleteRequest request, Authentication authentication) {
        deleteCommentProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CommentAnswerResponse>> getComments(Long answerId, Integer page) {
        return null;
    }

    @Override
    public ResponseEntity<List<CommentAnswerResponse>> getComments(CommentAnswerGetRequest request) {
        return null;
    }

    private Long createCommentProcess(CommentAnswerCreateRequest request, Authentication authentication) {
        validate(request);
        throwBadRequestExIfAnswerNotExist(request.getAnswerId());
        return saveNewComment(request, authentication);
    }

    private void editCommentProcess(CommentAnswerEditRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthor(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getCommentId());
        saveEditedComment(request);
    }

    private void deleteCommentProcess(CommentAnswerDeleteRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthor(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getCommentId());
        deleteCommentFromDatabase(request);
    }

    private Long saveNewComment(CommentAnswerCreateRequest request, Authentication authentication) {
        CommentAnswer commentAnswer = new CommentAnswer(
                request.getText(),
                new User(PrincipalUtil.getUserIdFromAuthentication(authentication)),
                new Answer(request.getAnswerId()));
        return commentAnswerDao.create(commentAnswer);
    }

    private void saveEditedComment(CommentAnswerEditRequest request) {
        CommentAnswer commentAnswer = new CommentAnswer();
        commentAnswer.setText(request.getText());
        commentAnswerDao.update(new Where("id", request.getCommentId(), WhereOperator.EQUALS), commentAnswer);
    }

    private void deleteCommentFromDatabase(CommentAnswerDeleteRequest request) {
        commentAnswerDao.delete(new Where("id", request.getCommentId(), WhereOperator.EQUALS));
    }

    private void checkIsRealAuthor(Long authenticationId, long commentId) {
        AuthorUtil.checkIsRealAuthorAndIsEntityExist(
                authenticationId,
                new Where("id", commentId, WhereOperator.EQUALS),
                CommentAnswer.class,
                commentAnswerDao,
                propertySetterFactory,
                logger,
                "comment");
    }

    private void throwBadRequestExIfAnswerNotExist(long answerId) {
        if (!isAnswerExist(answerId))
            throw new BadRequestException(ERR_MESSAGE_ANSWER_NOT_EXIST_ID.formatted(answerId));
    }

    private boolean isAnswerExist(long answerId) {
        return answerDao.isExist(answerId);
    }

    private void validate(CommentAnswerCreateRequest request) {
        ValidationUtil.validate(new CommentAnswerCreateRequestValidationWrapper(request, validationPropertyDataSource), validationChain);
    }

    private void validate(CommentAnswerEditRequest request) {
        ValidationUtil.validate(new CommentAnswerEditRequestValidationWrapper(request, validationPropertyDataSource), validationChain);
    }

    private void validate(CommentAnswerDeleteRequest request) {
        ValidationUtil.validate(new CommentAnswerDeleteRequestValidationWrapper(request), validationChain);
    }
}
