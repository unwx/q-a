package qa.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.AnswerDao;
import qa.dao.QuestionDao;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.request.answer.AnswerAnsweredRequest;
import qa.dto.request.answer.AnswerCreateRequest;
import qa.dto.request.answer.AnswerDeleteRequest;
import qa.dto.request.answer.AnswerEditRequest;
import qa.dto.validation.wrapper.answer.AnswerAnsweredRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerDeleteRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerEditRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.service.AnswerService;
import qa.source.ValidationPropertyDataSource;
import qa.util.AuthorUtil;
import qa.util.PrincipalUtil;
import qa.util.ValidationUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.Date;

@Service
public class AnswerServiceImpl implements AnswerService {

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

    private Long createAnswerProcess(AnswerCreateRequest request, Authentication authentication) {
        validate(request);
        badRequestIfQuestionNotExist(request.getId());
        return saveNewAnswer(request, authentication);
    }

    private void editAnswerProcess(AnswerEditRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthor(request.getId(), authentication);
        saveEditedAnswer(request);
    }

    private void setAnsweredProcess(AnswerAnsweredRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthor(request.getId(), authentication);
        saveAnswered(request);
    }

    private void removeAnsweredProcess(AnswerAnsweredRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthor(request.getId(), authentication);
        saveNotAnswered(request);
    }

    private void deleteAnswerProcess(AnswerDeleteRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthor(request.getId(), authentication);
        deleteAnswerFromDatabase(request);
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

    private void deleteAnswerFromDatabase(AnswerDeleteRequest request) {
        answerDao.delete(Answer.class, new Where("id", request.getId(), WhereOperator.EQUALS));
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

    private void badRequestIfQuestionNotExist(Long questionId) {
        if (!isQuestionExist(questionId))
            throw new BadRequestException("question not exist. id: " + questionId);
    }

    private boolean isQuestionExist(Long questionId) {
        Question q = questionDao.read(
                new Where("id", questionId, WhereOperator.EQUALS),
                new Table(new String[]{"id"}, "Question"));
        return q != null;
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
}
