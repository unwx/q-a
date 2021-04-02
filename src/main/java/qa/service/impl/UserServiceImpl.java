package qa.service.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import qa.dao.UserDao;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.dto.request.user.UserGetAnswersRequest;
import qa.dto.request.user.UserGetFullRequest;
import qa.dto.request.user.UserGetQuestionsRequest;
import qa.dto.response.user.UserAnswersResponse;
import qa.dto.response.user.UserFullResponse;
import qa.dto.response.user.UserQuestionsResponse;
import qa.dto.validation.wrapper.user.UserGetAnswersRequestValidationWrapper;
import qa.dto.validation.wrapper.user.UserGetFullRequestValidationWrapper;
import qa.dto.validation.wrapper.user.UserGetQuestionsRequestValidationWrapper;
import qa.exceptions.rest.ResourceNotFoundException;
import qa.service.UserService;
import qa.source.ValidationPropertyDataSource;
import qa.util.ValidationUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final ValidationChainAdditional validationChain;
    private final ValidationPropertyDataSource validationPropertyDataSource;

    public UserServiceImpl(UserDao userDao,
                           ValidationChainAdditional validationChain,
                           ValidationPropertyDataSource validationPropertyDataSource) {
        this.userDao = userDao;
        this.validationChain = validationChain;
        this.validationPropertyDataSource = validationPropertyDataSource;
    }

    @Override
    public ResponseEntity<UserFullResponse> getFullUser(String username) {
        return new ResponseEntity<>(getFullUserProcess(username), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserFullResponse> getFullUser(UserGetFullRequest request) {
        return new ResponseEntity<>(getFullUserProcess(request), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserQuestionsResponse>> getUserQuestions(Long userId, Integer page) {
        return new ResponseEntity<>(getUserQuestionsProcess(userId, page), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserQuestionsResponse>> getUserQuestions(UserGetQuestionsRequest request) {
        return new ResponseEntity<>(getUserQuestionsProcess(request), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserAnswersResponse>> getUserAnswers(Long userId, Integer page) {
        return new ResponseEntity<>(getUserAnswersProcess(userId, page), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserAnswersResponse>> getUserAnswers(UserGetAnswersRequest request) {
        return new ResponseEntity<>(getUserAnswersProcess(request), HttpStatus.OK);
    }

    private UserFullResponse getFullUserProcess(String username) {
        return getFullUserProcess(new UserGetFullRequest(username));
    }

    private UserFullResponse getFullUserProcess(UserGetFullRequest request) {
        validate(request);
        User user = getFullUserFromDatabase(request.getUsername());
        return new UserFullResponse(user.getId(), request.getUsername(), user.getAbout(), user.getQuestions(), user.getAnswers());
    }

    private List<UserQuestionsResponse> getUserQuestionsProcess(Long userId, Integer page) {
        return getUserQuestionsProcess(new UserGetQuestionsRequest(userId, page));
    }

    private List<UserQuestionsResponse> getUserQuestionsProcess(UserGetQuestionsRequest request) {
        validate(request);
        List<Question> questions = getUserQuestionsFromDatabase(request.getUserId(), request.getPage());
        return convertQuestionsToResponseDto(questions);
    }

    private List<UserAnswersResponse> getUserAnswersProcess(Long userId, Integer page) {
        return getUserAnswersProcess(new UserGetAnswersRequest(userId, page));
    }

    private List<UserAnswersResponse> getUserAnswersProcess(UserGetAnswersRequest request) {
        validate(request);
        List<Answer> answers = getUserAnswersFromDatabase(request.getUserId(), request.getPage());
        return convertAnswersToResponseDto(answers);
    }

    private User getFullUserFromDatabase(String username) {
        User user = userDao.readFullUser(username);
        if (user == null)
            throw new ResourceNotFoundException("user not exist. username: " + username);
        return user;
    }

    private List<Question> getUserQuestionsFromDatabase(Long userId, Integer page) {
        List<Question> questions = userDao.readUserQuestions(userId, page - 1); //client page start with 1. backend with 0
        if (questions == null)
            throw new ResourceNotFoundException("questions not found. userId: " + userId + ". page: " + page);
        return questions;
    }

    private List<Answer> getUserAnswersFromDatabase(Long userId, Integer page) {
        List<Answer> answers = userDao.readUserAnswers(userId, page - 1); //client page start with 1. backend with 0
        if (answers == null)
            throw new ResourceNotFoundException("answers not found. userId: " + userId + ". page: " + page);
        return answers;
    }

    private List<UserQuestionsResponse> convertQuestionsToResponseDto(@NotNull List<Question> questions) {
        List<UserQuestionsResponse> response = new ArrayList<>(questions.size());
        questions.forEach((q) -> response.add(new UserQuestionsResponse(q.getId(), q.getTitle())));
        return response;
    }

    private List<UserAnswersResponse> convertAnswersToResponseDto(@NotNull List<Answer> answers) {
        List<UserAnswersResponse> response = new ArrayList<>(answers.size());
        answers.forEach((a) -> response.add(new UserAnswersResponse(a.getId(), a.getText())));
        return response;
    }

    private void validate(UserGetFullRequest request) {
        ValidationUtil.validate(new UserGetFullRequestValidationWrapper(request, validationPropertyDataSource), validationChain);
    }

    private void validate(UserGetQuestionsRequest request) {
        ValidationUtil.validate(new UserGetQuestionsRequestValidationWrapper(request), validationChain);
    }

    private void validate(UserGetAnswersRequest request) {
        ValidationUtil.validate(new UserGetAnswersRequestValidationWrapper(request), validationChain);
    }
}
