package qa.service.impl.processor;

import org.springframework.stereotype.Component;
import qa.domain.AuthenticationData;
import qa.dto.internal.JwtDataDto;
import qa.dto.internal.JwtPairDataDto;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.response.JwtPairResponse;
import qa.service.impl.processor.manager.AuthenticationDataManager;
import qa.service.impl.processor.validator.AuthenticationRequestValidator;
import qa.service.util.JwtUtil;

@Component
public class AuthenticationServiceProcessor {

    private final AuthenticationRequestValidator validator;
    private final AuthenticationDataManager dataManager;
    private final JwtUtil jwtUtil;

    protected AuthenticationServiceProcessor(AuthenticationRequestValidator validator,
                                             AuthenticationDataManager dataManager,
                                             JwtUtil jwtUtil) {
        this.validator = validator;
        this.dataManager = dataManager;
        this.jwtUtil = jwtUtil;
    }

    protected JwtPairResponse loginProcess(AuthenticationRequest request) {
        this.validator.validate(request);
        final AuthenticationData data = new AuthenticationData.Builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        this.dataManager.authenticate(data);

        final JwtPairDataDto dto = this.getTokens(request.getEmail());
        this.dataManager.refreshUserTokensExpirationTime(request.getEmail(), dto);

        return new JwtPairResponse(dto.getAccess().getToken(), dto.getRefresh().getToken());
    }

    protected JwtPairResponse registrationProcess(RegistrationRequest request) {
        this.validator.validate(request);
        this.dataManager.checkExistence(request);

        final JwtPairDataDto dto = this.getTokens(request.getEmail());
        this.dataManager.saveNewUser(request, dto);
        return new JwtPairResponse(dto.getAccess().getToken(), dto.getRefresh().getToken());
    }

    protected JwtPairResponse refreshTokensProcess(String email) {
        final JwtPairDataDto dto = this.getTokens(email);
        this.dataManager.refreshUserTokensExpirationTime(email, dto);
        return new JwtPairResponse(dto.getAccess().getToken(), dto.getRefresh().getToken());
    }

    protected JwtPairDataDto getTokens(String email) {
        final JwtDataDto access = this.jwtUtil.createAccess(email);
        final JwtDataDto refresh = this.jwtUtil.createRefresh(email);
        return new JwtPairDataDto(access, refresh);
    }
}
