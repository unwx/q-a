package qa.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.CommentQuestionDao;
import qa.dao.QuestionDao;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.domain.CommentQuestion;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.request.comment.CommentQuestionCreateRequest;
import qa.dto.request.comment.CommentQuestionDeleteRequest;
import qa.dto.request.comment.CommentQuestionEditRequest;
import qa.dto.request.comment.CommentQuestionGetRequest;
import qa.dto.response.comment.CommentQuestionResponse;
import qa.dto.validation.wrapper.comment.CommentQuestionCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentQuestionDeleteRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.CommentQuestionEditRequestValidationWrapper;
import qa.dto.validation.wrapper.question.CommentQuestionGetRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.service.CommentQuestionService;
import qa.source.ValidationPropertyDataSource;
import qa.util.ResourceUtil;
import qa.util.ValidationUtil;
import qa.util.user.AuthorUtil;
import qa.util.user.PrincipalUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentQuestionServiceImpl implements CommentQuestionService {

    private final CommentQuestionDao commentQuestionDao;
    private final ValidationPropertyDataSource validationPropertyDataSource;
    private final ValidationChainAdditional validationChain;
    private final PropertySetterFactory propertySetterFactory;
    private final QuestionDao questionDao;

    private static final String ERR_MESSAGE_QUESTION_NOT_EXIST_ID = "question not exist. question id: %s";

    private static final Logger logger = LogManager.getLogger(CommentQuestionServiceImpl.class);

    @Autowired
    public CommentQuestionServiceImpl(CommentQuestionDao commentQuestionDao,
                                      ValidationPropertyDataSource validationPropertyDataSource,
                                      ValidationChainAdditional validationChain,
                                      PropertySetterFactory propertySetterFactory,
                                      QuestionDao questionDao) {
        this.commentQuestionDao = commentQuestionDao;
        this.validationPropertyDataSource = validationPropertyDataSource;
        this.validationChain = validationChain;
        this.propertySetterFactory = propertySetterFactory;
        this.questionDao = questionDao;
    }

    @Override
    public ResponseEntity<Long> createComment(CommentQuestionCreateRequest request, Authentication authentication) {
        return new ResponseEntity<>(createCommentProcess(request, authentication), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> editComment(CommentQuestionEditRequest request, Authentication authentication) {
        editCommentProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteComment(CommentQuestionDeleteRequest request, Authentication authentication) {
        deleteCommentProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CommentQuestionResponse>> getComments(Long questionId, Integer page) {
        return new ResponseEntity<>(getCommentProcess(questionId, page), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CommentQuestionResponse>> getComments(CommentQuestionGetRequest request) {
        return new ResponseEntity<>(getCommentProcess(request), HttpStatus.OK);
    }

    private Long createCommentProcess(CommentQuestionCreateRequest request, Authentication authentication) {
        validate(request);
        throwBadRequestExIfQuestionNotExist(request.getQuestionId());
        return saveNewComment(request, authentication);
    }

    private void editCommentProcess(CommentQuestionEditRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthorCommentQuestion(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getCommentId());
        saveEditedComment(request);
    }

    private void deleteCommentProcess(CommentQuestionDeleteRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthorCommentQuestion(PrincipalUtil.getUserIdFromAuthentication(authentication), request.getCommentId());
        deleteCommentFromDatabase(request);
    }

    private List<CommentQuestionResponse> getCommentProcess(Long questionId, Integer page) {
        return getCommentProcess(new CommentQuestionGetRequest(questionId, page));
    }

    private List<CommentQuestionResponse> getCommentProcess(CommentQuestionGetRequest request) {
        validate(request);
        List<CommentQuestion> comments = getCommentFromDatabase(request.getQuestionId(), request.getPage());
        return convertCommentDtoToResponse(comments);
    }

    private Long saveNewComment(CommentQuestionCreateRequest request, Authentication authentication) {
        CommentQuestion commentQuestion = new CommentQuestion(
                request.getText(),
                new User(PrincipalUtil.getUserIdFromAuthentication(authentication)),
                new Question(request.getQuestionId()));
        return commentQuestionDao.create(commentQuestion);
    }

    private void saveEditedComment(CommentQuestionEditRequest request) {
        CommentQuestion commentQuestion = new CommentQuestion();
        commentQuestion.setText(request.getText());
        commentQuestionDao.update(new Where("id", request.getCommentId(), WhereOperator.EQUALS), commentQuestion);
    }

    private void deleteCommentFromDatabase(CommentQuestionDeleteRequest request) {
        commentQuestionDao.delete(new Where("id", request.getCommentId(), WhereOperator.EQUALS));
    }

    private List<CommentQuestion> getCommentFromDatabase(long questionId, int page) {
        List<CommentQuestion> questions = commentQuestionDao.getComments(questionId, page);
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

    private List<CommentQuestionResponse> convertCommentDtoToResponse(List<CommentQuestion> comments) {
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
            throw new BadRequestException(ERR_MESSAGE_QUESTION_NOT_EXIST_ID.formatted(questionId));
    }

    private boolean isQuestionExist(long questionId) {
        return questionDao.isExist(questionId);
    }

    private void validate(CommentQuestionCreateRequest request) {
        ValidationUtil.validate(new CommentQuestionCreateRequestValidationWrapper(request, validationPropertyDataSource), validationChain);
    }

    private void validate(CommentQuestionEditRequest request) {
        ValidationUtil.validate(new CommentQuestionEditRequestValidationWrapper(request, validationPropertyDataSource), validationChain);
    }

    private void validate(CommentQuestionDeleteRequest request) {
        ValidationUtil.validate(new CommentQuestionDeleteRequestValidationWrapper(request), validationChain);
    }

    private void validate(CommentQuestionGetRequest request) {
        ValidationUtil.validate(new CommentQuestionGetRequestValidationWrapper(request), validationChain);
    }
}
