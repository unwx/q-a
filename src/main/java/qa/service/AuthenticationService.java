package qa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import qa.dao.AuthenticationDao;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.domain.AuthenticationData;
import qa.domain.User;
import qa.domain.UserRoles;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.response.JwtPairResponseDto;
import qa.dto.internal.JwtDataDto;
import qa.dto.internal.JwtPairDataDto;
import qa.dto.validation.wrapper.authentication.AuthenticationRequestValidationWrapper;
import qa.dto.validation.wrapper.authentication.RegistrationRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.exceptions.rest.UnauthorizedException;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.util.JwtUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.Collections;

@Service
public class AuthenticationService {

    private final AuthenticationDao authenticationDao;
    private final ValidationPropertyDataSource propertiesDataSource;
    private final ValidationChainAdditional chainValidator;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthenticationService(AuthenticationDao authenticationDao,
                                 ValidationPropertyDataSource propertiesDataSource,
                                 ValidationChainAdditional chainValidator,
                                 JwtUtil jwtUtil) {
        this.authenticationDao = authenticationDao;
        this.propertiesDataSource = propertiesDataSource;
        this.chainValidator = chainValidator;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<JwtPairResponseDto> login(AuthenticationRequest request) {
        return new ResponseEntity<>(loginProcess(request), HttpStatus.OK);
    }

    public ResponseEntity<JwtPairResponseDto> registration(RegistrationRequest request) {
        return new ResponseEntity<>(registrationProcess(request), HttpStatus.OK);
    }

    public ResponseEntity<JwtPairResponseDto> refreshTokens(String email) {
        return new ResponseEntity<>(refreshTokensProcess(email), HttpStatus.OK);
    }

    private JwtPairResponseDto loginProcess(AuthenticationRequest request) {
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

    private JwtPairResponseDto registrationProcess(RegistrationRequest request) {
        validate(request);
        alreadyExistException(request);
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

    private void validate(AuthenticationRequest request) {
        AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(request, propertiesDataSource);
        try {
            chainValidator.validateWithAdditionalValidator(validationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void validate(RegistrationRequest request) {
        RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(request, propertiesDataSource);
        try {
            chainValidator.validateWithAdditionalValidator(validationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void authenticate(AuthenticationData data) {
        if (!authenticationDao.isEmailPasswordCorrect(data.getEmail(), data.getPassword())) {
            throw new UnauthorizedException("incorrect login or password");
        }
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
        authenticationDao.update(new Where("email", email, WhereOperator.EQUALS), data, "AuthenticationData");
    }

    private boolean isUserAlreadyExist(RegistrationRequest request) {
        String[] fieldsName = new String[] {
                "id"
        };
        AuthenticationData data = authenticationDao.read(
                new Where("email", request.getEmail(), WhereOperator.EQUALS),
                new Table(fieldsName, "AuthenticationData"));
        return data != null;
    }

    private void alreadyExistException(RegistrationRequest request) {
        if (isUserAlreadyExist(request))
            throw new BadRequestException("user already exist.");
    }
}
