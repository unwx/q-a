package qa.service.impl.aid.process;

import org.springframework.stereotype.Component;
import qa.domain.AuthenticationData;
import qa.dto.internal.JwtDataDto;
import qa.dto.internal.JwtPairDataDto;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.response.JwtPairResponse;
import qa.service.impl.aid.process.database.AuthenticationServiceDatabase;
import qa.service.impl.aid.process.validation.AuthenticationServiceValidation;
import qa.service.util.JwtUtil;

@Component
public class AuthenticationServiceProcess {

    private final AuthenticationServiceValidation validation;
    private final AuthenticationServiceDatabase database;
    private final JwtUtil jwtUtil;

    protected AuthenticationServiceProcess(AuthenticationServiceValidation validation,
                                        AuthenticationServiceDatabase database,
                                        JwtUtil jwtUtil) {
        this.validation = validation;
        this.database = database;
        this.jwtUtil = jwtUtil;
    }

    protected JwtPairResponse loginProcess(AuthenticationRequest request) {
        this.validation.validate(request);
        final AuthenticationData data = new AuthenticationData.Builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        this.database.authenticate(data);

        final JwtPairDataDto dto = this.getTokens(request.getEmail());
        this.database.refreshUserTokensExpirationTime(request.getEmail(), dto);

        return new JwtPairResponse(dto.getAccess().getToken(), dto.getRefresh().getToken());
    }

    protected JwtPairResponse registrationProcess(RegistrationRequest request) {
        this.validation.validate(request);
        this.database.checkExistence(request);

        final JwtPairDataDto dto = this.getTokens(request.getEmail());
        this.database.saveNewUser(request, dto);
        return new JwtPairResponse(dto.getAccess().getToken(), dto.getRefresh().getToken());
    }

    protected JwtPairResponse refreshTokensProcess(String email) {
        final JwtPairDataDto dto = this.getTokens(email);
        this.database.refreshUserTokensExpirationTime(email, dto);
        return new JwtPairResponse(dto.getAccess().getToken(), dto.getRefresh().getToken());
    }

    protected JwtPairDataDto getTokens(String email) {
        final JwtDataDto access = this.jwtUtil.createAccess(email);
        final JwtDataDto refresh = this.jwtUtil.createRefresh(email);
        return new JwtPairDataDto(access, refresh);
    }
}
