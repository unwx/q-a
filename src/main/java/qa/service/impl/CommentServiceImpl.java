package qa.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.AnswerDao;
import qa.dao.CommentAnswerDao;
import qa.dao.CommentQuestionDao;
import qa.dao.QuestionDao;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.domain.*;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.request.comment.*;
import qa.dto.validation.wrapper.comment.CommentAnswerCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentQuestionCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentQuestionEditRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.service.CommentService;
import qa.source.ValidationPropertyDataSource;
import qa.util.AuthorUtil;
import qa.util.PrincipalUtil;
import qa.util.ValidationUtil;
import qa.validators.abstraction.ValidationChainAdditional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentQuestionDao commentQuestionDao;
    private final CommentAnswerDao commentAnswerDao;
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;
    private final ValidationPropertyDataSource validationPropertyDataSource;
    private final ValidationChainAdditional validationChain;
    private final PropertySetterFactory propertySetterFactory;
    private final Logger logger = LogManager.getLogger(CommentServiceImpl.class);

    public CommentServiceImpl(CommentQuestionDao commentQuestionDao,
                              CommentAnswerDao commentAnswerDao,
                              QuestionDao questionDao,
                              AnswerDao answerDao,
                              ValidationPropertyDataSource validationPropertyDataSource,
                              ValidationChainAdditional validationChain,
                              PropertySetterFactory propertySetterFactory) {
        this.commentQuestionDao = commentQuestionDao;
        this.commentAnswerDao = commentAnswerDao;
        this.questionDao = questionDao;
        this.answerDao = answerDao;
        this.validationPropertyDataSource = validationPropertyDataSource;
        this.validationChain = validationChain;
        this.propertySetterFactory = propertySetterFactory;
    }

    @Override
    public ResponseEntity<Long> createCommentQuestion(CommentQuestionCreateRequest request, Authentication authentication) {
        return new ResponseEntity<>(createCommentQuestionProcess(request, authentication), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Long> createCommentAnswer(CommentAnswerCreateRequest request, Authentication authentication) {
        return new ResponseEntity<>(createCommentAnswerProcess(request, authentication), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> editCommentQuestion(CommentQuestionEditRequest request, Authentication authentication) {
        editCommentQuestionProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
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

    private Long createCommentQuestionProcess(CommentQuestionCreateRequest request, Authentication authentication) {
        validate(request);
        throwBadRequestExIfQuestionNotExist(request.getQuestionId());
        return saveNewCommentQuestion(request, authentication);
    }

    private Long createCommentAnswerProcess(CommentAnswerCreateRequest request, Authentication authentication) {
        validate(request);
        throwBadRequestExIfAnswerNotExist(request.getAnswerId());
        return saveNewCommentAnswer(request, authentication);
    }

    private void editCommentQuestionProcess(CommentQuestionEditRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthorCommentQuestion(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getId());
        saveEditedCommentQuestion(request);
    }

    private Long saveNewCommentQuestion(CommentQuestionCreateRequest request, Authentication authentication) {
        CommentQuestion commentQuestion = new CommentQuestion(
                request.getText(),
                new User(PrincipalUtil.getUserIdFromAuthentication(authentication)),
                new Question(request.getQuestionId()));
        return commentQuestionDao.create(commentQuestion);
    }

    private Long saveNewCommentAnswer(CommentAnswerCreateRequest request, Authentication authentication) {
        CommentAnswer commentAnswer = new CommentAnswer(
                request.getText(),
                new User(PrincipalUtil.getUserIdFromAuthentication(authentication)),
                new Answer(request.getAnswerId()));
        return commentAnswerDao.create(commentAnswer);
    }

    private void saveEditedCommentQuestion(CommentQuestionEditRequest request) {
        CommentQuestion commentQuestion = new CommentQuestion();
        commentQuestion.setText(request.getText());
        commentQuestionDao.update(new Where("id", request.getId(), WhereOperator.EQUALS), commentQuestion);
    }

    private void checkIsRealAuthorCommentQuestion(Long authenticationId, Long commentId) {
        AuthorUtil.checkIsRealAuthorAndIsEntityExist(
                authenticationId,
                new Where("id", commentId, WhereOperator.EQUALS),
                CommentQuestion.class,
                commentQuestionDao,
                propertySetterFactory,
                logger,
                "comment");
    }

    private void throwBadRequestExIfQuestionNotExist(Long questionId) {
        if (!isQuestionExist(questionId))
            throw new BadRequestException("question not exist. id: " + questionId);
    }

    private void throwBadRequestExIfAnswerNotExist(Long answerId) {
        if (!isAnswerExist(answerId))
            throw new BadRequestException("answer not exist. id: " + answerId);
    }

    private boolean isQuestionExist(Long questionId) {
        return questionDao.isExist(questionId);
    }

    private boolean isAnswerExist(Long answerId) {
        return answerDao.isExist(answerId);
    }

    private void validate(CommentQuestionCreateRequest request) {
        ValidationUtil.validate(new CommentQuestionCreateRequestValidationWrapper(request, validationPropertyDataSource), validationChain);
    }

    private void validate(CommentAnswerCreateRequest request) {
        ValidationUtil.validate(new CommentAnswerCreateRequestValidationWrapper(request, validationPropertyDataSource), validationChain);
    }

    private void validate(CommentQuestionEditRequest request) {
        ValidationUtil.validate(new CommentQuestionEditRequestValidationWrapper(request, validationPropertyDataSource), validationChain);
    }
}
