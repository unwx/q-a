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
import qa.domain.QuestionView;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.request.question.*;
import qa.dto.response.question.QuestionFullResponse;
import qa.dto.response.question.QuestionViewResponse;
import qa.dto.validation.wrapper.question.*;
import qa.service.QuestionService;
import qa.source.ValidationPropertyDataSource;
import qa.util.QuestionTagsUtil;
import qa.util.ResourceUtil;
import qa.util.ValidationUtil;
import qa.util.user.AuthorUtil;
import qa.util.user.PrincipalUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionDao questionDao;
    private final ValidationPropertyDataSource validationPropertyDataSource;
    private final ValidationChainAdditional validationChain;
    private final PropertySetterFactory propertySetterFactory;

    private static final String ERR_MESSAGE_QUESTION_NOT_EXIST_ID = "question not exist. question id: %s";

    private static final Logger logger = LogManager.getLogger(QuestionServiceImpl.class);

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

    @Override
    public ResponseEntity<List<QuestionViewResponse>> getQuestions(Integer page) {
        return new ResponseEntity<>(getQuestionsProcess(page), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<QuestionViewResponse>> getQuestions(QuestionGetViewsRequest request) {
        return new ResponseEntity<>(getQuestionsProcess(request), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<QuestionFullResponse> getFullQuestion(Long questionId, Authentication authentication) {
        return new ResponseEntity<>(getFullQuestionProcess(questionId, authentication), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<QuestionFullResponse> getFullQuestion(QuestionGetFullRequest request, Authentication authentication) {
        return new ResponseEntity<>(getFullQuestionProcess(request, authentication), HttpStatus.OK);
    }

    private Long createQuestionProcess(QuestionCreateRequest request, Authentication authentication) {
        validate(request);
        return saveNewQuestion(request, authentication);
    }

    private void editQuestionProcess(QuestionEditRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthor(request.getQuestionId(), authentication);
        saveEditedQuestion(request);
    }

    private void deleteQuestionProcess(QuestionDeleteRequest request, Authentication authentication) {
        validate(request);
        checkIsRealAuthor(request.getQuestionId(), authentication);
        deleteQuestionById(request.getQuestionId());
    }

    private List<QuestionViewResponse> getQuestionsProcess(Integer page) {
        return getQuestionsProcess(new QuestionGetViewsRequest(page));
    }

    private List<QuestionViewResponse> getQuestionsProcess(QuestionGetViewsRequest request) {
        validate(request);
        List<QuestionView> views = getQuestionViewsFromDatabase(request.getPage());
        return convertViewDtoToResponse(views);
    }

    private QuestionFullResponse getFullQuestionProcess(Long questionId, Authentication authentication) {
        return getFullQuestionProcess(new QuestionGetFullRequest(questionId), authentication);
    }

    private QuestionFullResponse getFullQuestionProcess(QuestionGetFullRequest request, Authentication authentication) {
        validate(request);
        final long userId = authentication == null ? -1L : getUserIdFromAuthentication(authentication);
        final Question question = getFullQuestionFromDatabase(request.getQuestionId(), userId);
        return convertDtoToResponse(question);
    }

    private Long saveNewQuestion(QuestionCreateRequest request, Authentication authentication) {
        Question question = new Question.Builder()
                .creationDate(new Date())
                .lastActivity(new Date())
                .tags(QuestionTagsUtil.tagsToString(request.getTags()))
                .text(request.getText())
                .title(request.getTitle())
                .author(new User(PrincipalUtil.getUserIdFromAuthentication(authentication)))
                .build();
        return questionDao.create(question);
    }

    private void checkIsRealAuthor(long id, Authentication authentication) {
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
                new Where("id", request.getQuestionId(), WhereOperator.EQUALS),
                new Question.Builder()
                        .text(request.getText())
                        .tags(QuestionTagsUtil.tagsToString(request.getTags()))
                        .lastActivity(new Date())
                        .build()
        );
    }

    private void deleteQuestionById(long questionId) {
        questionDao.delete(new Where("id", questionId, WhereOperator.EQUALS));
    }

    private List<QuestionView> getQuestionViewsFromDatabase(int page) {
        return questionDao.getQuestionViewsDto(page - 1);
    }

    private Question getFullQuestionFromDatabase(long questionId, long userId) {
        final Question question = questionDao.getFullQuestion(questionId, userId);
        return ResourceUtil.throwResourceNFExceptionIfNull(question, ERR_MESSAGE_QUESTION_NOT_EXIST_ID.formatted(questionId));
    }

    private long getUserIdFromAuthentication(Authentication authentication) {
        final Long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        return userId == null ? -1 : userId;
    }

    private List<QuestionViewResponse> convertViewDtoToResponse(List<QuestionView> views) {
        List<QuestionViewResponse> viewsResponse = new ArrayList<>(views.size());
        views.forEach((v) -> viewsResponse.add(
                new QuestionViewResponse(
                        v.getQuestionId(),
                        v.getTitle(),
                        QuestionTagsUtil.stringToTags(v.getTags()),
                        v.getCreationDate(),
                        v.getLastActivity(),
                        v.getAnswersCount(),
                        v.getAuthor())
        ));
        return viewsResponse;
    }

    private QuestionFullResponse convertDtoToResponse(Question question) {
        return new QuestionFullResponse(
                question.getId(),
                question.getText(),
                question.getTitle(),
                question.getCreationDate(),
                question.getLastActivity(),
                QuestionTagsUtil.stringToTags(question.getTags()),
                question.getAuthor(),
                question.getAnswers(),
                question.getComments() // TODO likes
        );
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

    private void validate(QuestionGetViewsRequest request) {
        ValidationUtil.validate(new QuestionGetViewsRequestValidationWrapper(request), validationChain);
    }

    private void validate(QuestionGetFullRequest request) {
        ValidationUtil.validate(new QuestionGetFullRequestValidationWrapper(request), validationChain);
    }
}
