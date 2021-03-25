package qa.service.impl;

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
import qa.service.QuestionService;
import qa.source.ValidationPropertyDataSource;
import qa.util.AuthorUtil;
import qa.util.PrincipalUtil;
import qa.util.ValidationUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.Arrays;
import java.util.Date;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionDao questionDao;
    private final ValidationPropertyDataSource validationPropertyDataSource;
    private final ValidationChainAdditional validationChain;
    private final PropertySetterFactory propertySetterFactory;

    private final static Logger logger = LogManager.getLogger(QuestionServiceImpl.class);

    public QuestionServiceImpl(QuestionDao questionDao,
                               ValidationPropertyDataSource validationPropertyDataSource,
                               ValidationChainAdditional validationChain,
                               PropertySetterFactory propertySetterFactory) {
        this.questionDao = questionDao;
        this.validationPropertyDataSource = validationPropertyDataSource;
        this.validationChain = validationChain;
        this.propertySetterFactory = propertySetterFactory;
    }

    @Override
    public ResponseEntity<Long> createQuestion(QuestionCreateRequest request, Authentication authentication) {
        return new ResponseEntity<>(createQuestionProcess(request, authentication), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> editQuestion(QuestionEditRequest request, Authentication authentication) {
        editQuestionProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteQuestion(QuestionDeleteRequest request, Authentication authentication) {
        deleteQuestionProcess(request, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Long createQuestionProcess(QuestionCreateRequest request, Authentication authentication) {
        validate(request);
        return saveNewQuestion(request, authentication);
    }

    private void editQuestionProcess(QuestionEditRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthor(request.getId(), authentication);
        saveEditedQuestion(request);
    }

    private void deleteQuestionProcess(QuestionDeleteRequest request, Authentication authentication) {
        validate(request);
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
        AuthorUtil.checkIsRealAuthorAndIsEntityExist(
                PrincipalUtil.getUserIdFromAuthentication(authentication),
                new Where("id", id, WhereOperator.EQUALS),
                Question.class,
                questionDao,
                propertySetterFactory,
                logger,
                "question");
    }

    private void saveEditedQuestion(QuestionEditRequest request) {
        questionDao.update(
                new Where("id", request.getId(), WhereOperator.EQUALS),
                new Question.Builder()
                        .text(request.getText())
                        .tags(tagsArrayToString(request.getTags()))
                        .lastActivity(new Date())
                        .build()
        );
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

    private void validate(QuestionCreateRequest request) {
        ValidationUtil.validateWithAdditional(new QuestionCreateRequestValidationWrapper(request, validationPropertyDataSource), validationChain);
    }

    private void validate(QuestionEditRequest request) {
        ValidationUtil.validateWithAdditional(new QuestionEditRequestValidationWrapper(request, validationPropertyDataSource), validationChain);
    }

    private void validate(QuestionDeleteRequest request) {
        ValidationUtil.validate(new QuestionDeleteRequestValidationWrapper(request), validationChain);
    }
}
