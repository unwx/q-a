package qa.service;

import org.springframework.http.ResponseEntity;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.response.JwtPairResponseDto;

public interface AuthenticationService {
    ResponseEntity<JwtPairResponseDto> login(AuthenticationRequest request);

    ResponseEntity<JwtPairResponseDto> registration(RegistrationRequest request);

    ResponseEntity<JwtPairResponseDto> refreshTokens(String email);
}
