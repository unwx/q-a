package qa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import qa.dto.request.user.UserGetAnswersRequest;
import qa.dto.request.user.UserGetFullRequest;
import qa.dto.request.user.UserGetQuestionsRequest;
import qa.dto.response.user.UserAnswersResponse;
import qa.dto.response.user.UserFullResponse;
import qa.dto.response.user.UserQuestionsResponse;
import qa.service.UserService;
import qa.service.impl.aid.process.UserServiceProcess;
import qa.service.impl.aid.process.database.UserServiceDatabase;
import qa.service.impl.aid.process.validation.UserServiceValidation;

import java.util.List;

@Service
public class UserServiceImpl extends UserServiceProcess implements UserService {

    @Autowired
    public UserServiceImpl(UserServiceValidation validation,
                           UserServiceDatabase database) {
        super(validation, database);
    }

    @Override
    public ResponseEntity<UserFullResponse> getFullUser(String username) {
        final UserFullResponse response = super.getFullUserProcess(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserFullResponse> getFullUser(UserGetFullRequest request) {
        final UserFullResponse response = super.getFullUserProcess(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserQuestionsResponse>> getUserQuestions(Long userId, Integer page) {
        final List<UserQuestionsResponse> response = super.getUserQuestionsProcess(userId, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserQuestionsResponse>> getUserQuestions(UserGetQuestionsRequest request) {
        final List<UserQuestionsResponse> response = super.getUserQuestionsProcess(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserAnswersResponse>> getUserAnswers(Long userId, Integer page) {
        final List<UserAnswersResponse> response = super.getUserAnswersProcess(userId, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserAnswersResponse>> getUserAnswers(UserGetAnswersRequest request) {
        final List<UserAnswersResponse> response = super.getUserAnswersProcess(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
