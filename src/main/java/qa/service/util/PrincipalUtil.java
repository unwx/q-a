package qa.service.util;

import org.springframework.security.core.Authentication;
import qa.security.jwt.entity.JwtAuthenticationData;

public class PrincipalUtil {

    private PrincipalUtil() {}

    public static long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null)
            return -1L;
        final JwtAuthenticationData data = (JwtAuthenticationData) authentication.getPrincipal();
        return data.getId();
    }
}
