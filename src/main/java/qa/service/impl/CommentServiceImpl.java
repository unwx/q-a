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
import qa.dto.request.question.QuestionGetCommentsRequest;
import qa.dto.response.comment.CommentQuestionResponse;
import qa.dto.validation.wrapper.comment.*;
import qa.dto.validation.wrapper.question.QuestionGetCommentsRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.service.CommentService;
import qa.source.ValidationPropertyDataSource;
import qa.util.ResourceUtil;
import qa.util.ValidationUtil;
import qa.util.user.AuthorUtil;
import qa.util.user.PrincipalUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentQuestionDao commentQuestionDao;
    private final CommentAnswerDao commentAnswerDao;
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;
    private final ValidationPropertyDataSource validationPropertyDataSource;
    private final ValidationChainAdditional validationChain;
    private final PropertySetterFactory propertySetterFactory;

    private static final String ERR_MESSAGE_QUESTION_NOT_EXIST_ID = "question not exist. question id: %s";

    private static final Logger logger = LogManager.getLogger(CommentServiceImpl.class);

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
        editCommentAnswerProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteCommentQuestion(CommentQuestionDeleteRequest request, Authentication authentication) {
        deleteCommentQuestionProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteCommentAnswer(CommentAnswerDeleteRequest request, Authentication authentication) {
        deleteCommentAnswerProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CommentQuestionResponse>> getCommentQuestion(Long questionId, Integer page) {
        return new ResponseEntity<>(getCommentQuestionProcess(questionId, page), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CommentQuestionResponse>> getCommentQuestion(QuestionGetCommentsRequest request) {
        return new ResponseEntity<>(getCommentQuestionProcess(request), HttpStatus.OK);
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
        checkIsRealAuthorCommentQuestion(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getCommentId());
        saveEditedCommentQuestion(request);
    }

    private void editCommentAnswerProcess(CommentAnswerEditRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthorCommentAnswer(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getCommentId());
        saveEditedCommentAnswer(request);
    }

    private void deleteCommentQuestionProcess(CommentQuestionDeleteRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthorCommentQuestion(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getCommentId());
        deleteCommentQuestionFromDatabase(request);
    }

    private void deleteCommentAnswerProcess(CommentAnswerDeleteRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthorCommentAnswer(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getCommentId());
        deleteCommentAnswerFromDatabase(request);
    }

    private List<CommentQuestionResponse> getCommentQuestionProcess(Long questionId, Integer page) {
        return getCommentQuestionProcess(new QuestionGetCommentsRequest(questionId, page));
    }

    private List<CommentQuestionResponse> getCommentQuestionProcess(QuestionGetCommentsRequest request) {
        validate(request);
        List<CommentQuestion> comments = getCommentQuestionFromDatabase(request.getQuestionId(), request.getPage());
        return convertCommentQuestionDtoToResponse(comments);
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
        commentQuestionDao.update(new Where("id", request.getCommentId(), WhereOperator.EQUALS), commentQuestion);
    }

    private void saveEditedCommentAnswer(CommentAnswerEditRequest request) {
        CommentAnswer commentAnswer = new CommentAnswer();
        commentAnswer.setText(request.getText());
        commentAnswerDao.update(new Where("id", request.getCommentId(), WhereOperator.EQUALS), commentAnswer);
    }

    private void deleteCommentQuestionFromDatabase(CommentQuestionDeleteRequest request) {
        commentQuestionDao.delete(new Where("id", request.getCommentId(), WhereOperator.EQUALS));
    }

    private void deleteCommentAnswerFromDatabase(CommentAnswerDeleteRequest request) {
        commentAnswerDao.delete(new Where("id", request.getCommentId(), WhereOperator.EQUALS));
    }
    private List<CommentQuestion> getCommentQuestionFromDatabase(long questionId, int page) {
        List<CommentQuestion> questions = questionDao.getQuestionComments(questionId, page);
        return ResourceUtil.throwResourceNFExceptionIfNull(questions, ERR_MESSAGE_QUESTION_NOT_EXIST_ID.formatted(questions));
    }

    private void checkIsRealAuthorCommentQuestion(Long authenticationId, long commentId) {
        AuthorUtil.checkIsRealAuthorAndIsEntityExist(
                authenticationId,
                new Where("id", commentId, WhereOperator.EQUALS),
                CommentQuestion.class,
                commentQuestionDao,
                propertySetterFactory,
                logger,
                "comment");
    }

    private void checkIsRealAuthorCommentAnswer(Long authenticationId, long commentId) {
        AuthorUtil.checkIsRealAuthorAndIsEntityExist(
                authenticationId,
                new Where("id", commentId, WhereOperator.EQUALS),
                CommentAnswer.class,
                commentAnswerDao,
                propertySetterFactory,
                logger,
                "comment");
    }

    private List<CommentQuestionResponse> convertCommentQuestionDtoToResponse(List<CommentQuestion> comments) {
        List<CommentQuestionResponse> response = new ArrayList<>(comments.size());
        comments.forEach((c) -> response.add(
                new CommentQuestionResponse(
                        c.getId(),
                        c.getText(),
                        c.getCreationDate(),
                        c.getAuthor()
                )
        ));
        return response;
    }

    private void throwBadRequestExIfQuestionNotExist(long questionId) {
        if (!isQuestionExist(questionId))
            throw new BadRequestException("question not exist. id: " + questionId);
    }

    private void throwBadRequestExIfAnswerNotExist(long answerId) {
        if (!isAnswerExist(answerId))
            throw new BadRequestException("answer not exist. id: " + answerId);
    }

    private boolean isQuestionExist(long questionId) {
        return questionDao.isExist(questionId);
    }

    private boolean isAnswerExist(long answerId) {
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

    private void validate(CommentAnswerEditRequest request) {
        ValidationUtil.validate(new CommentAnswerEditRequestValidationWrapper(request, validationPropertyDataSource), validationChain);
    }

    private void validate(CommentQuestionDeleteRequest request) {
        ValidationUtil.validate(new CommentQuestionDeleteRequestValidationWrapper(request), validationChain);
    }

    private void validate(CommentAnswerDeleteRequest request) {
        ValidationUtil.validate(new CommentAnswerDeleteRequestValidationWrapper(request), validationChain);
    }

    private void validate(QuestionGetCommentsRequest request) {
        ValidationUtil.validate(new QuestionGetCommentsRequestValidationWrapper(request), validationChain);
    }
}
