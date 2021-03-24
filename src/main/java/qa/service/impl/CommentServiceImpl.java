package qa.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.AnswerDao;
import qa.dao.CommentAnswerDao;
import qa.dao.CommentQuestionDao;
import qa.dao.QuestionDao;
import qa.domain.*;
import qa.dto.request.comment.*;
import qa.dto.validation.wrapper.comment.CommentAnswerCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentQuestionCreateRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.service.CommentService;
import qa.source.ValidationPropertyDataSource;
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

    public CommentServiceImpl(CommentQuestionDao commentQuestionDao,
                              CommentAnswerDao commentAnswerDao,
                              QuestionDao questionDao,
                              AnswerDao answerDao,
                              ValidationPropertyDataSource validationPropertyDataSource,
                              ValidationChainAdditional validationChain) {
        this.commentQuestionDao = commentQuestionDao;
        this.commentAnswerDao = commentAnswerDao;
        this.questionDao = questionDao;
        this.answerDao = answerDao;
        this.validationPropertyDataSource = validationPropertyDataSource;
        this.validationChain = validationChain;
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
}
