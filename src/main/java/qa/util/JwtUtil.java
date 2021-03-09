package qa.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dto.service.JwtDataDto;
import qa.security.jwt.entity.JwtData;
import qa.security.jwt.service.JwtProvider;

@Component
public class JwtUtil {

    private final JwtProvider provider;

    @Autowired
    public JwtUtil(JwtProvider provider) {
        this.provider = provider;
    }

    public JwtDataDto createAccess(String email) {
        JwtData data = provider.createAccess(email);
        return new JwtDataDto("Bearer_" + data.getToken(), data.getExpirationAtMillis());
    }

    public JwtDataDto createRefresh(String email) {
        JwtData data = provider.createRefresh(email);
        return new JwtDataDto("Bearer_" + data.getToken(), data.getExpirationAtMillis());
    }
}
