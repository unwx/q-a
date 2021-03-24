package qa.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.CommentQuestionDao;
import qa.dao.QuestionDao;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.domain.Question;
import qa.domain.CommentQuestion;
import qa.domain.User;
import qa.dto.request.comment.*;
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
    private final QuestionDao questionDao;
    private final ValidationPropertyDataSource validationPropertyDataSource;
    private final ValidationChainAdditional validationChain;

    public CommentServiceImpl(CommentQuestionDao commentQuestionDao,
                              QuestionDao questionDao,
                              ValidationPropertyDataSource validationPropertyDataSource,
                              ValidationChainAdditional validationChain) {
        this.commentQuestionDao = commentQuestionDao;
        this.questionDao = questionDao;
        this.validationPropertyDataSource = validationPropertyDataSource;
        this.validationChain = validationChain;
    }

    @Override
    public ResponseEntity<Long> createCommentQuestion(CommentQuestionCreateRequest request, Authentication authentication) {
        return new ResponseEntity<>(createCommentQuestionProcess(request, authentication), HttpStatus.OK);
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

    private Long createCommentQuestionProcess(CommentQuestionCreateRequest request, Authentication authentication) {
        validate(request);
        throwBadRequestExIfQuestionNotExist(request);
        return saveNewCommentQuestion(request, authentication);
    }

    private Long saveNewCommentQuestion(CommentQuestionCreateRequest request, Authentication authentication) {
        CommentQuestion commentQuestion = new CommentQuestion(
                request.getText(),
                new User(PrincipalUtil.getUserIdFromAuthentication(authentication)),
                new Question(request.getQuestionId()));
        return commentQuestionDao.create(commentQuestion);
    }

    private void throwBadRequestExIfQuestionNotExist(CommentQuestionCreateRequest request) {
        if (!isQuestionExist(request))
            throw new BadRequestException("question not exist. id: " + request.getQuestionId());
    }

    private boolean isQuestionExist(CommentQuestionCreateRequest request) {
        Question q = questionDao.read(
                new Where("id", request.getQuestionId(), WhereOperator.EQUALS),
                new Table(new String[]{"id"}, "Question"));
        return q != null;
    }

    private void validate(CommentQuestionCreateRequest request) {
        ValidationUtil.validate(new CommentQuestionCreateRequestValidationWrapper(request, validationPropertyDataSource), validationChain);
    }
}
