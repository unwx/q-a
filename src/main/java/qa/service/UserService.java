package qa.service;

import org.springframework.http.ResponseEntity;
import qa.dto.request.user.UserGetFullRequest;
import qa.dto.request.user.UserGetQuestionsRequest;
import qa.dto.response.user.FullUserResponse;
import qa.dto.response.user.UserQuestionsResponse;

import java.util.List;

public interface UserService {
    ResponseEntity<FullUserResponse> getFullUser(String username);

    ResponseEntity<FullUserResponse> getFullUser(UserGetFullRequest request);

    ResponseEntity<List<UserQuestionsResponse>> getUserQuestions(Long userId, Integer startPage);

    ResponseEntity<List<UserQuestionsResponse>> getUserQuestions(UserGetQuestionsRequest request);
}
