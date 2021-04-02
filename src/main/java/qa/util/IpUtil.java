package qa.util;

import javax.servlet.http.HttpServletRequest;

public final class IpUtil {

    private IpUtil() {
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        String[] headers = new String[] {
                request.getHeader("X-Forwarded-For"),
                request.getHeader("Proxy-Client-IP"),
                request.getHeader("WL-Proxy-Client-IP"),
                request.getHeader("HTTP_CLIENT_IP"),
                request.getHeader("HTTP_X_FORWARDED_FOR"),
        };

        for (String s : headers) {
            if (s != null && s.length() != 0 && !"unknown".equalsIgnoreCase(s))
                return s;
        }
        return request.getRemoteAddr();
    }
}
