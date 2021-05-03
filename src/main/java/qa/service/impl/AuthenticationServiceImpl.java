package qa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.response.JwtPairResponse;
import qa.service.AuthenticationService;
import qa.service.impl.processor.AuthenticationServiceProcessor;
import qa.service.impl.processor.manager.AuthenticationDataManager;
import qa.service.impl.processor.validator.AuthenticationRequestValidator;
import qa.service.util.JwtUtil;

@Service
public class AuthenticationServiceImpl extends AuthenticationServiceProcessor implements AuthenticationService {

    @Autowired
    public AuthenticationServiceImpl(AuthenticationRequestValidator validation,
                                     AuthenticationDataManager database,
                                     JwtUtil jwtUtil) {
        super(validation, database, jwtUtil);
    }

    @Override
    public ResponseEntity<JwtPairResponse> login(AuthenticationRequest request) {
        final JwtPairResponse jwt = super.loginProcess(request);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<JwtPairResponse> registration(RegistrationRequest request) {
        final JwtPairResponse jwt = super.registrationProcess(request);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<JwtPairResponse> refreshTokens(String email) {
        final JwtPairResponse jwt = super.refreshTokensProcess(email);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }
}
