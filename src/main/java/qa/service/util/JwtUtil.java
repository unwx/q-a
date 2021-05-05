package qa.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dto.internal.JwtDataDto;
import qa.security.jwt.entity.JwtData;
import qa.security.jwt.service.JwtProvider;

@Component
public class JwtUtil {

    private final JwtProvider provider;

    private static final String BEARER = "Bearer_";

    @Autowired
    public JwtUtil(JwtProvider provider) {
        this.provider = provider;
    }

    public JwtDataDto createAccess(String email) {
        final JwtData data = provider.createAccess(email);
        return new JwtDataDto(BEARER + data.getToken(), data.getExpirationAtMillis());
    }

    public JwtDataDto createRefresh(String email) {
        final JwtData data = provider.createRefresh(email);
        return new JwtDataDto(BEARER + data.getToken(), data.getExpirationAtMillis());
    }
}
