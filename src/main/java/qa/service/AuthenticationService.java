package qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import qa.dao.AuthenticationDao;
import qa.dao.databasecomponents.Field;
import qa.domain.AuthenticationData;
import qa.domain.User;
import qa.domain.UserRoles;
import qa.dto.request.AuthenticationRequestDto;
import qa.dto.request.RegistrationRequestDto;
import qa.dto.response.JwtPairResponseDto;
import qa.dto.service.JwtDataDto;
import qa.dto.service.JwtPairDataDto;
import qa.dto.validation.wrapper.AuthenticationRequestValidationWrapper;
import qa.dto.validation.wrapper.RegistrationRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.exceptions.validator.ValidationException;
import qa.source.PropertiesDataSource;
import qa.util.JwtUtil;
import qa.validators.ChainValidatorImpl;
import qa.validators.additional.EmailValidator;

import java.util.Collections;

@Service
public class AuthenticationService {

    private final AuthenticationDao authenticationDao;
    private final PropertiesDataSource propertiesDataSource;
    private final ChainValidatorImpl chainValidator;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthenticationService(AuthenticationDao authenticationDao,
                                 PropertiesDataSource propertiesDataSource,
                                 ChainValidatorImpl chainValidator,
                                 AuthenticationManager authenticationManager,
                                 JwtUtil jwtUtil) {
        this.authenticationDao = authenticationDao;
        this.propertiesDataSource = propertiesDataSource;
        this.chainValidator = chainValidator;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<JwtPairResponseDto> login(AuthenticationRequestDto request) {
        return new ResponseEntity<>(loginProcess(request), HttpStatus.OK);
    }

    public ResponseEntity<JwtPairResponseDto> registration(RegistrationRequestDto request) {
        return new ResponseEntity<>(registrationProcess(request), HttpStatus.OK);
    }

    public ResponseEntity<JwtPairResponseDto> refreshTokens(String email) {
        return new ResponseEntity<>(refreshTokensProcess(email), HttpStatus.OK);
    }

    private JwtPairResponseDto loginProcess(AuthenticationRequestDto request) {
        validate(request);
        AuthenticationData data = new AuthenticationData.Builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        authenticate(data);
        JwtPairDataDto dto = getTokens(request.getEmail());
        refreshUserTokensExpirationTime(request.getEmail(), dto);
        return new JwtPairResponseDto(dto.getAccess().getToken(), dto.getRefresh().getToken());
    }

    private JwtPairResponseDto registrationProcess(RegistrationRequestDto request) {
        validate(request);
        JwtPairDataDto dto = getTokens(request.getEmail());
        User user = new User.Builder()
                .username(request.getUsername())
                .build();
        AuthenticationData data = new AuthenticationData.Builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .enabled(true)
                .accessTokenExpirationDateAtMillis(dto.getAccess().getExp())
                .refreshTokenExpirationDateAtMillis(dto.getRefresh().getExp())
                .user(user)
                .roles(Collections.singletonList(UserRoles.USER))
                .build();
        authenticationDao.create(data);
        return new JwtPairResponseDto(dto.getAccess().getToken(), dto.getRefresh().getToken());
    }

    private JwtPairResponseDto refreshTokensProcess(String email) {
        JwtPairDataDto dto = getTokens(email);
        refreshUserTokensExpirationTime(email, dto);
        return new JwtPairResponseDto(dto.getAccess().getToken(), dto.getRefresh().getToken());
    }

    private void validate(AuthenticationRequestDto request) {
        AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(request, propertiesDataSource);
        try {
            chainValidator.validateWithAdditionalValidator(validationWrapper, validationWrapper.getEmail(), new EmailValidator());
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void validate(RegistrationRequestDto request) {
        RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(request, propertiesDataSource);
        try {
            chainValidator.validateWithAdditionalValidator(validationWrapper, validationWrapper.getEmail(), new EmailValidator());
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void authenticate(AuthenticationData data) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword()));
    }

    private JwtPairDataDto getTokens(String email) {
        JwtDataDto access = jwtUtil.createAccess(email);
        JwtDataDto refresh = jwtUtil.createRefresh(email);
        return new JwtPairDataDto(access, refresh);
    }

    private void refreshUserTokensExpirationTime(String email, JwtPairDataDto jwtPair) {
        AuthenticationData data = new AuthenticationData.Builder()
                .accessTokenExpirationDateAtMillis(jwtPair.getAccess().getExp())
                .refreshTokenExpirationDateAtMillis(jwtPair.getRefresh().getExp())
                .build();
        authenticationDao.update(new Field("email", email), data, "AuthenticationData");
    }
}
