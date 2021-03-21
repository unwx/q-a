package qa.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import qa.dao.QuestionDao;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.request.QuestionCreateRequest;
import qa.dto.request.QuestionDeleteRequest;
import qa.dto.request.QuestionEditRequest;
import qa.dto.validation.wrapper.QuestionCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.QuestionDeleteRequestValidationWrapper;
import qa.dto.validation.wrapper.QuestionEditRequestValidationWrapper;
import qa.exceptions.rest.AccessDeniedException;
import qa.exceptions.rest.BadRequestException;
import qa.exceptions.service.internal.AuthorNotExistException;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.util.PrincipalUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Service
public class QuestionService {

    private final QuestionDao questionDao;
    private final ValidationPropertyDataSource validationPropertyDataSource;
    private final ValidationChainAdditional validationChain;
    private final PropertySetterFactory propertySetterFactory;

    private final Logger logger = LogManager.getLogger(QuestionService.class);

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

    public ResponseEntity<Integer> editQuestion(QuestionEditRequest request, Authentication authentication) {
        editQuestionProcess(request, authentication);
        return new ResponseEntity<>(200, HttpStatus.OK);
    }

    public ResponseEntity<Integer> deleteQuestion(QuestionDeleteRequest request, Authentication authentication) {
        deleteQuestionProcess(request, authentication);
        return new ResponseEntity<>(200, HttpStatus.OK);
    }

    private Long createQuestionProcess(QuestionCreateRequest request, Authentication authentication) {
        validationProcess(request);
        return saveNewQuestion(request, authentication);
    }

    private void editQuestionProcess(QuestionEditRequest request, Authentication authentication) {
        validationProcess(request);
        checkIsRealAuthor(request.getId(), PrincipalUtil.getUserIdFromAuthentication(authentication));
        saveEditedQuestion(request);
    }

    private void deleteQuestionProcess(QuestionDeleteRequest request, Authentication authentication) {
        validationProcess(request);
        checkIsRealAuthor(request.getId(), PrincipalUtil.getUserIdFromAuthentication(authentication));
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

    private void checkIsRealAuthor(Long requestId, Long userId) {
        if (!isRealAuthor(requestId, userId)) {
            throw new AccessDeniedException("you do not have permission to this question");
        }
    }

    private boolean isRealAuthor(Long requestId, Long userId) {
        Question question = questionDao.read(
                new Where("id", requestId, WhereOperator.EQUALS),
                new Table(new String[]{}, "Question"),
                Collections.singletonList(new NestedEntity(new String[]{"id"}, User.class, "author", propertySetterFactory.getSetter(new User())))
        );
        if (question == null)
            questionNotExistProcess(requestId);

        if (question.getAuthor() == null)
            authorNotExistProcess(requestId);

        return question.getAuthor().getId().equals(userId);
    }

    private void questionNotExistProcess(Long id) {
        throw new BadRequestException("this question doesn't exist. id: " + id);
    }

    private void authorNotExistProcess(Long id) {
        String message = "question id: " + id + ". Author not exist";
        logger.error(message);
        throw new AuthorNotExistException(message);
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
