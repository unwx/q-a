package qa.service;

import org.springframework.http.ResponseEntity;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.response.JwtPairResponse;

public interface AuthenticationService {
    ResponseEntity<JwtPairResponse> login(AuthenticationRequest request);

    ResponseEntity<JwtPairResponse> registration(RegistrationRequest request);

    ResponseEntity<JwtPairResponse> refreshTokens(String email);
}
