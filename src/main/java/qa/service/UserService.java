package qa.service;

import org.springframework.http.ResponseEntity;
import qa.dto.request.user.UserGetFullRequest;
import qa.dto.response.user.FullUserResponse;

public interface UserService {
    ResponseEntity<FullUserResponse> getFullUser(String username);

    ResponseEntity<FullUserResponse> getFullUser(UserGetFullRequest request);
}
