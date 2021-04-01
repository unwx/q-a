package qa.service;

import org.springframework.http.ResponseEntity;
import qa.dto.request.user.UserGetAnswersRequest;
import qa.dto.request.user.UserGetFullRequest;
import qa.dto.request.user.UserGetQuestionsRequest;
import qa.dto.response.user.UserAnswersResponse;
import qa.dto.response.user.UserFullResponse;
import qa.dto.response.user.UserQuestionsResponse;

import java.util.List;

public interface UserService {
    ResponseEntity<UserFullResponse> getFullUser(String username);

    ResponseEntity<UserFullResponse> getFullUser(UserGetFullRequest request);

    ResponseEntity<List<UserQuestionsResponse>> getUserQuestions(Long userId, Integer startPage);

    ResponseEntity<List<UserQuestionsResponse>> getUserQuestions(UserGetQuestionsRequest request);

    ResponseEntity<List<UserAnswersResponse>> getUserAnswers(Long userId, Integer startPage);

    ResponseEntity<List<UserAnswersResponse>> getUserAnswers(UserGetAnswersRequest request);
}
