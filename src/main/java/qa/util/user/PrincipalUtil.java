package qa.util.user;

import org.springframework.security.core.Authentication;
import qa.security.jwt.entity.JwtAuthenticationData;

public class PrincipalUtil {

    private PrincipalUtil() {
    }

    public static Long getUserIdFromAuthentication(Authentication authentication) {
        JwtAuthenticationData data = (JwtAuthenticationData) authentication.getPrincipal();
        return data.getId();
    }
}
