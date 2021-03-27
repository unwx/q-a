package qa.service.impl;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
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
import qa.dto.internal.JwtDataDto;
import qa.dto.internal.JwtPairDataDto;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.response.JwtPairResponse;
import qa.dto.validation.wrapper.authentication.AuthenticationRequestValidationWrapper;
import qa.dto.validation.wrapper.authentication.RegistrationRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.exceptions.rest.UnauthorizedException;
import qa.security.PasswordEncryptorFactory;
import qa.service.AuthenticationService;
import qa.source.ValidationPropertyDataSource;
import qa.util.JwtUtil;
import qa.util.ValidationUtil;
import qa.validators.abstraction.ValidationChainAdditional;

import java.util.Collections;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationDao authenticationDao;
    private final ValidationPropertyDataSource propertiesDataSource;
    private final ValidationChainAdditional chainValidator;
    private final JwtUtil jwtUtil;
    private final PooledPBEStringEncryptor passwordEncryptor;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationDao authenticationDao,
                                     ValidationPropertyDataSource propertiesDataSource,
                                     ValidationChainAdditional chainValidator,
                                     JwtUtil jwtUtil,
                                     PasswordEncryptorFactory passwordEncryptorFactory) {
        this.authenticationDao = authenticationDao;
        this.propertiesDataSource = propertiesDataSource;
        this.chainValidator = chainValidator;
        this.jwtUtil = jwtUtil;
        this.passwordEncryptor = passwordEncryptorFactory.create();
    }

    @Override
    public ResponseEntity<JwtPairResponse> login(AuthenticationRequest request) {
        return new ResponseEntity<>(loginProcess(request), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<JwtPairResponse> registration(RegistrationRequest request) {
        return new ResponseEntity<>(registrationProcess(request), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<JwtPairResponse> refreshTokens(String email) {
        return new ResponseEntity<>(refreshTokensProcess(email), HttpStatus.OK);
    }

    private JwtPairResponse loginProcess(AuthenticationRequest request) {
        validate(request);
        AuthenticationData data = new AuthenticationData.Builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        authenticate(data);
        JwtPairDataDto dto = getTokens(request.getEmail());
        refreshUserTokensExpirationTime(request.getEmail(), dto);
        return new JwtPairResponse(dto.getAccess().getToken(), dto.getRefresh().getToken());
    }

    private JwtPairResponse registrationProcess(RegistrationRequest request) {
        validate(request);
        alreadyExistException(request);
        JwtPairDataDto dto = getTokens(request.getEmail());
        saveNewUser(request, dto);
        return new JwtPairResponse(dto.getAccess().getToken(), dto.getRefresh().getToken());
    }

    private void saveNewUser(RegistrationRequest request, JwtPairDataDto dto) {
        User user = new User.Builder()
                .username(request.getUsername())
                .build();

        AuthenticationData data = new AuthenticationData.Builder()
                .email(request.getEmail())
                .password(passwordEncryptor.encrypt(request.getPassword()))
                .enabled(true)
                .accessTokenExpirationDateAtMillis(dto.getAccess().getExp())
                .refreshTokenExpirationDateAtMillis(dto.getRefresh().getExp())
                .user(user)
                .roles(Collections.singletonList(UserRoles.USER))
                .build();
        authenticationDao.create(data);
    }

    private JwtPairResponse refreshTokensProcess(String email) {
        JwtPairDataDto dto = getTokens(email);
        refreshUserTokensExpirationTime(email, dto);
        return new JwtPairResponse(dto.getAccess().getToken(), dto.getRefresh().getToken());
    }

    private void authenticate(AuthenticationData data) {
        if (!authenticationDao.isEmailPasswordCorrect(data.getEmail(), data.getPassword(), passwordEncryptor)) {
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
        authenticationDao.update(new Where("email", email, WhereOperator.EQUALS), data);
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

    private boolean isUsernameAlreadyExist(RegistrationRequest request) {
        String[] fieldsName = new String[] {
                "id"
        };
        AuthenticationData data = authenticationDao.read(
                new Where("username", request.getUsername(), WhereOperator.EQUALS),
                new Table(fieldsName, "User"));
        return data != null;
    }

    private void alreadyExistException(RegistrationRequest request) {
        if (isUserAlreadyExist(request))
            throw new BadRequestException("user already exist.");
        if (isUsernameAlreadyExist(request))
            throw new BadRequestException("user with this username already exist");
    }

    private void validate(AuthenticationRequest request) {
        ValidationUtil.validateWithAdditional(new AuthenticationRequestValidationWrapper(request, propertiesDataSource), chainValidator);
    }

    private void validate(RegistrationRequest request) {
        ValidationUtil.validateWithAdditional(new RegistrationRequestValidationWrapper(request, propertiesDataSource), chainValidator);
    }
}
