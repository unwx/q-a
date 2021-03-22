package qa.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.QuestionDao;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.request.question.QuestionCreateRequest;
import qa.dto.request.question.QuestionDeleteRequest;
import qa.dto.request.question.QuestionEditRequest;
import qa.dto.validation.wrapper.question.QuestionCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.question.QuestionDeleteRequestValidationWrapper;
import qa.dto.validation.wrapper.question.QuestionEditRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.util.AuthorUtil;
import qa.util.PrincipalUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.Arrays;
import java.util.Date;

@Service
public class QuestionService {

    private final QuestionDao questionDao;
    private final ValidationPropertyDataSource validationPropertyDataSource;
    private final ValidationChainAdditional validationChain;
    private final PropertySetterFactory propertySetterFactory;

    private final static Logger logger = LogManager.getLogger(QuestionService.class);

    public QuestionService(QuestionDao questionDao,
                           ValidationPropertyDataSource validationPropertyDataSource,
                           ValidationChainAdditional validationChain,
                           PropertySetterFactory propertySetterFactory) {
        this.questionDao = questionDao;
        this.validationPropertyDataSource = validationPropertyDataSource;
        this.validationChain = validationChain;
        this.propertySetterFactory = propertySetterFactory;
    }

    public ResponseEntity<Long> createQuestion(QuestionCreateRequest request, Authentication authentication) {
        return new ResponseEntity<>(createQuestionProcess(request, authentication), HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> editQuestion(QuestionEditRequest request, Authentication authentication) {
        editQuestionProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> deleteQuestion(QuestionDeleteRequest request, Authentication authentication) {
        deleteQuestionProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Long createQuestionProcess(QuestionCreateRequest request, Authentication authentication) {
        validationProcess(request);
        return saveNewQuestion(request, authentication);
    }

    private void editQuestionProcess(QuestionEditRequest request, Authentication authentication) {
        validationProcess(request);
        checkIsRealAuthor(request.getId(), authentication);
        saveEditedQuestion(request);
    }

    private void deleteQuestionProcess(QuestionDeleteRequest request, Authentication authentication) {
        validationProcess(request);
        checkIsRealAuthor(request.getId(), authentication);
        deleteQuestionById(request.getId());
    }

    private Long saveNewQuestion(QuestionCreateRequest request, Authentication authentication) {
        Question question = new Question.Builder()
                .creationDate(new Date())
                .lastActivity(new Date())
                .tags(tagsArrayToString(request.getTags()))
                .text(request.getText())
                .title(request.getTitle())
                .author(new User(PrincipalUtil.getUserIdFromAuthentication(authentication)))
                .build();
        return questionDao.create(question);
    }

    private void checkIsRealAuthor(Long id, Authentication authentication) {
        AuthorUtil.checkIsRealAuthor(
                PrincipalUtil.getUserIdFromAuthentication(authentication),
                new Where("id", id, WhereOperator.EQUALS),
                Question.class,
                questionDao,
                propertySetterFactory,
                logger);
    }

    private void saveEditedQuestion(QuestionEditRequest request) {
        questionDao.update(
                new Where("id", request.getId(), WhereOperator.EQUALS),
                new Question.Builder()
                        .text(request.getText())
                        .tags(tagsArrayToString(request.getTags()))
                        .lastActivity(new Date())
                        .build(),
                "Question");
    }

    private void deleteQuestionById(Long questionId) {
        questionDao.delete(Question.class, new Where("id", questionId, WhereOperator.EQUALS));
    }

    private String tagsArrayToString(String[] tags) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(tags).forEach((t) -> sb.append(t).append(","));
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private void validationProcess(QuestionCreateRequest request) {
        QuestionCreateRequestValidationWrapper requestValidationWrapper = new QuestionCreateRequestValidationWrapper(request, validationPropertyDataSource);
        try {
            validationChain.validateWithAdditionalValidator(requestValidationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void validationProcess(QuestionEditRequest request) {
        QuestionEditRequestValidationWrapper requestValidationWrapper = new QuestionEditRequestValidationWrapper(request, validationPropertyDataSource);
        try {
            validationChain.validateWithAdditionalValidator(requestValidationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void validationProcess(QuestionDeleteRequest request) {
        QuestionDeleteRequestValidationWrapper requestValidationWrapper = new QuestionDeleteRequestValidationWrapper(request);
        try {
            validationChain.validate(requestValidationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
