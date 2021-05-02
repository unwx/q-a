package qa.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.AnswerDao;
import qa.dao.QuestionDao;
import qa.dao.database.components.Where;
import qa.dao.database.components.WhereOperator;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.request.answer.*;
import qa.dto.response.answer.AnswerFullResponse;
import qa.dto.validation.wrapper.answer.*;
import qa.exceptions.rest.AccessDeniedException;
import qa.exceptions.rest.BadRequestException;
import qa.service.AnswerService;
import qa.service.err.ServiceExceptionMessage;
import qa.service.util.AuthorUtil;
import qa.service.util.PrincipalUtil;
import qa.service.util.ResourceUtil;
import qa.service.util.ValidationUtil;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerServiceImpl implements AnswerService { // TODO REFACTOR

    private final AnswerDao answerDao;
    private final QuestionDao questionDao;
    private final ValidationPropertyDataSource propertyDataSource;
    private final ValidationChainAdditional validationChain;
    private final PropertySetterFactory propertySetterFactory;

    private static final Logger logger = LogManager.getLogger(AnswerServiceImpl.class);

    public AnswerServiceImpl(AnswerDao answerDao,
                             QuestionDao questionDao,
                             ValidationPropertyDataSource propertyDataSource,
                             ValidationChainAdditional validationChain,
                             PropertySetterFactory propertySetterFactory) {
        this.answerDao = answerDao;
        this.questionDao = questionDao;
        this.propertyDataSource = propertyDataSource;
        this.validationChain = validationChain;
        this.propertySetterFactory = propertySetterFactory;
    }

    @Override
    public ResponseEntity<Long> createAnswer(AnswerCreateRequest request, Authentication authentication) {
        return new ResponseEntity<>(createAnswerProcess(request, authentication), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> editAnswer(AnswerEditRequest request, Authentication authentication) {
        editAnswerProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> setAnswered(AnswerAnsweredRequest request, Authentication authentication) {
        setAnsweredProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> removeAnswered(AnswerAnsweredRequest request, Authentication authentication) {
        removeAnsweredProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteAnswer(AnswerDeleteRequest request, Authentication authentication) {
        deleteAnswerProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<AnswerFullResponse>> getAnswers(Long questionId, Integer page, Authentication authentication) {
        return new ResponseEntity<>(getAnswersProcess(questionId, page, authentication), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<AnswerFullResponse>> getAnswers(AnswerGetFullRequest request, Authentication authentication) {
        return new ResponseEntity<>(getAnswersProcess(request, authentication), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> like(AnswerLikeRequest likeRequest, Authentication authentication) {
        this.likeProcess(likeRequest, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Long createAnswerProcess(AnswerCreateRequest request, Authentication authentication) {
        validate(request);
        throwBadRequestExIfQuestionNotExist(request.getQuestionId());
        return saveNewAnswer(request, authentication);
    }

    private void editAnswerProcess(AnswerEditRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthor(request.getAnswerId(), authentication);
        saveEditedAnswer(request);
    }

    private void setAnsweredProcess(AnswerAnsweredRequest request, Authentication authentication) {
        validate(request);
        checkIsQuestionAuthor(request.getAnswerId(), authentication);
        saveAnswered(request);
    }

    private void removeAnsweredProcess(AnswerAnsweredRequest request, Authentication authentication) {
        validate(request);
        checkIsQuestionAuthor(request.getAnswerId(), authentication);
        saveNotAnswered(request);
    }

    private void deleteAnswerProcess(AnswerDeleteRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthor(request.getAnswerId(), authentication);
        deleteAnswerFromDatabase(request);
    }

    private List<AnswerFullResponse> getAnswersProcess(Long questionId, Integer page, Authentication authentication) {
        return this.getAnswersProcess(new AnswerGetFullRequest(questionId, page), authentication);
    }

    private List<AnswerFullResponse> getAnswersProcess(AnswerGetFullRequest request, Authentication authentication) {
        this.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        final List<Answer> answers = this.getAnswersFromDatabase(request.getQuestionId(), userId, request.getPage());
        return this.convertToDto(answers);
    }

    private void likeProcess(AnswerLikeRequest request, Authentication authentication) {
        this.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        this.answerDao.like(userId, request.getAnswerId());
    }

    private Long saveNewAnswer(AnswerCreateRequest request, Authentication authentication) {
        Answer answer = new Answer.Builder()
                .text(request.getText())
                .answered(false)
                .creationDate(new Date())
                .author(new User(PrincipalUtil.getUserIdFromAuthentication(authentication)))
                .question(new Question(request.getQuestionId()))
                .build();
        return answerDao.create(answer);
    }

    private void saveEditedAnswer(AnswerEditRequest request) {
        Answer answer = new Answer.Builder()
                .text(request.getText())
                .build();

        answerDao.update(new Where("id", request.getAnswerId(), WhereOperator.EQUALS), answer);
    }

    private void saveAnswered(AnswerAnsweredRequest request) {
        answerDao.update(
                new Where("id", request.getAnswerId(), WhereOperator.EQUALS),
                new Answer.Builder().answered(true).build()
        );
    }

    private void saveNotAnswered(AnswerAnsweredRequest request) {
        answerDao.update(
                new Where("id", request.getAnswerId(), WhereOperator.EQUALS),
                new Answer.Builder().answered(false).build()
        );
    }

    private void deleteAnswerFromDatabase(AnswerDeleteRequest request) {
        answerDao.delete(new Where("id", request.getAnswerId(), WhereOperator.EQUALS));
    }

    private List<Answer> getAnswersFromDatabase(long questionId, long userId, int page) {
        final List<Answer> answers = this.answerDao.getAnswers(questionId, userId, page - 1);
        return ResourceUtil.throwResourceNFExceptionIfNull(answers, ServiceExceptionMessage.ERR_MESSAGE_QUESTION_NOT_EXIST_ID.formatted(questionId));
    }

    private void checkIsRealAuthor(Long answerId, Authentication authentication) {
        AuthorUtil.checkIsRealAuthorAndIsEntityExist(
                PrincipalUtil.getUserIdFromAuthentication(authentication),
                new Where("id", answerId, WhereOperator.EQUALS),
                new Answer(),
                answerDao,
                propertySetterFactory,
                logger,
                "answer");
    }

    private void checkIsQuestionAuthor(long answerId, Authentication authentication) {
        final Long realAuthorId = questionDao.getQuestionAuthorIdFromAnswer(answerId);
        final long authenticationId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        if (realAuthorId != authenticationId)
            throw new AccessDeniedException("you do not have permission to this answer");
    }

    private List<AnswerFullResponse> convertToDto(List<Answer> answers) {
        return answers.stream().map((a) -> new AnswerFullResponse(
                a.getId(),
                a.getText(),
                a.getCreationDate(),
                a.getAnswered(),
                a.getAuthor(),
                a.getComments(),
                a.getLikes(),
                a.isLiked()
                )).collect(Collectors.toList());
    }

    private void throwBadRequestExIfQuestionNotExist(Long questionId) {
        if (!isQuestionExist(questionId))
            throw new BadRequestException("question not exist. id: " + questionId);
    }

    private boolean isQuestionExist(Long questionId) {
        return questionDao.isExist(questionId);
    }

    private void validate(AnswerCreateRequest request) {
        ValidationUtil.validate(new AnswerCreateRequestValidationWrapper(request, propertyDataSource), validationChain);
    }

    private void validate(AnswerEditRequest request) {
        ValidationUtil.validate(new AnswerEditRequestValidationWrapper(request, propertyDataSource), validationChain);
    }

    private void validate(AnswerAnsweredRequest request) {
        ValidationUtil.validate(new AnswerAnsweredRequestValidationWrapper(request), validationChain);
    }

    private void validate(AnswerDeleteRequest request) {
        ValidationUtil.validate(new AnswerDeleteRequestValidationWrapper(request), validationChain);
    }

    private void validate(AnswerGetFullRequest request) {
        ValidationUtil.validate(new AnswerGetFullRequestValidationWrapper(request), validationChain);
    }

    private void validate(AnswerLikeRequest request) {
        ValidationUtil.validate(new AnswerLikeRequestValidationWrapper(request), validationChain);
    }
}
