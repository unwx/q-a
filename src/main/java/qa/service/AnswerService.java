package qa.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.AnswerDao;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.request.answer.AnswerAnsweredRequest;
import qa.dto.request.answer.AnswerCreateRequest;
import qa.dto.request.answer.AnswerEditRequest;
import qa.dto.validation.wrapper.answer.AnswerAnsweredRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerEditRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.util.AuthorUtil;
import qa.util.PrincipalUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.Date;

@Service
public class AnswerService {

    private final AnswerDao answerDao;
    private final ValidationPropertyDataSource propertyDataSource;
    private final ValidationChainAdditional validationChain;
    private final PropertySetterFactory propertySetterFactory;

    private static final Logger logger = LogManager.getLogger(AnswerService.class);

    public AnswerService(AnswerDao answerDao,
                         ValidationPropertyDataSource propertyDataSource,
                         ValidationChainAdditional validationChain,
                         PropertySetterFactory propertySetterFactory) {
        this.answerDao = answerDao;
        this.propertyDataSource = propertyDataSource;
        this.validationChain = validationChain;
        this.propertySetterFactory = propertySetterFactory;
    }

    public ResponseEntity<Long> createAnswer(AnswerCreateRequest request, Authentication authentication) {
        return new ResponseEntity<>(createAnswerProcess(request, authentication), HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> editAnswer(AnswerEditRequest request, Authentication authentication) {
        editAnswerProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> setAnswered(AnswerAnsweredRequest request, Authentication authentication) {
        setAnsweredProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> removeAnswered(AnswerAnsweredRequest request, Authentication authentication) {
        removeAnsweredProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Long createAnswerProcess(AnswerCreateRequest request, Authentication authentication) {
        validationProcess(request);
        return saveNewAnswer(request, authentication);
    }

    private void editAnswerProcess(AnswerEditRequest request, Authentication authentication) {
        validationProcess(request);
        checkIsRealAuthor(request.getId(), authentication);
        saveEditedAnswer(request);
    }

    private void setAnsweredProcess(AnswerAnsweredRequest request, Authentication authentication) {
        validationProcess(request);
        checkIsRealAuthor(request.getId(), authentication);
        saveAnswered(request);
    }

    private void removeAnsweredProcess(AnswerAnsweredRequest request, Authentication authentication) {
        validationProcess(request);
        checkIsRealAuthor(request.getId(), authentication);
        saveNotAnswered(request);
    }

    private Long saveNewAnswer(AnswerCreateRequest request, Authentication authentication) {
        Answer answer = new Answer.Builder()
                .text(request.getText())
                .answered(false)
                .creationDate(new Date())
                .author(new User(PrincipalUtil.getUserIdFromAuthentication(authentication)))
                .question(new Question(request.getId()))
                .build();
        return answerDao.create(answer);
    }

    private void saveEditedAnswer(AnswerEditRequest request) {
        Answer answer = new Answer.Builder()
                .text(request.getText())
                .build();

        answerDao.update(new Where("id", request.getId(), WhereOperator.EQUALS), answer, "Answer");
    }

    private void saveAnswered(AnswerAnsweredRequest request) {
        answerDao.update(
                new Where("id", request.getId(), WhereOperator.EQUALS),
                new Answer.Builder().answered(true).build(),
                "Answer");
    }

    private void saveNotAnswered(AnswerAnsweredRequest request) {
        answerDao.update(
                new Where("id", request.getId(), WhereOperator.EQUALS),
                new Answer.Builder().answered(false).build(),
                "Answer");
    }

    private void checkIsRealAuthor(Long id, Authentication authentication) {
        AuthorUtil.checkIsRealAuthor(
                PrincipalUtil.getUserIdFromAuthentication(authentication),
                new Where("id", id, WhereOperator.EQUALS),
                Answer.class,
                answerDao,
                propertySetterFactory,
                logger);
    }

    private void validationProcess(AnswerCreateRequest request) {
        AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(request, propertyDataSource);
        try {
            validationChain.validate(validationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void validationProcess(AnswerEditRequest request) {
        AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(request, propertyDataSource);
        try {
            validationChain.validate(validationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void validationProcess(AnswerAnsweredRequest request) {
        AnswerAnsweredRequestValidationWrapper validationWrapper = new AnswerAnsweredRequestValidationWrapper(request);
        try {
            validationChain.validate(validationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
