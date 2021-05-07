package qa.security.jwt.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import qa.security.jwt.entity.*;
import qa.security.jwt.filter.util.Ipv4Util;
import qa.security.jwt.service.JwtProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;

    private static final Logger logger = LogManager.getLogger(JwtFilter.class);
    private static final String ERR_INVALID_TOKEN = "The token is not valid.";
    private static final String ERR_EXPIRED_TOKEN = "the token is expired.";
    private static final String WARN_LOG =
            """
            [invalid token]: the token is not valid.\s\
            IPv4: %s\s\
            User-Agent: %s\s\
            URI: %s\
            """;
    private static final String ERR_MESSAGE =
            """
            {\
            "status": 401,
            "timestamp": %s,
            "message": "%s",
            "description": null
            }\
            """;

    public JwtFilter(JwtProvider jwtTokenProvider) {
        this.jwtProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest.getAttribute(PreJwtFilter.EXIST_TOKEN_ATTRIBUTE).equals(true)) {
            final String cleanToken = jwtProvider.resolveToken((HttpServletRequest) servletRequest);
            final JwtIntermediateDateTransport validationResult = jwtProvider.validate(cleanToken);

            if (validationResult.getStatus() == JwtStatus.VALID) {
                if (validationResult.getType() == JwtType.ACCESS && !((HttpServletRequest) servletRequest).getRequestURI().equals("/api/v1/authentication/refresh")) {
                    this.authentication(validationResult.getData());
                }
                if (validationResult.getType() == JwtType.REFRESH && ((HttpServletRequest) servletRequest).getRequestURI().equals("/api/v1/authentication/refresh-tokens")) {
                    this.putClaimsInServletRequest(servletRequest, validationResult.getClaims());
                    this.authentication(validationResult.getData());
                }

            } else if (validationResult.getStatus() == JwtStatus.INVALID) {
                this.invalidTokenProcess(servletResponse, ERR_INVALID_TOKEN);
                this.logInvalidToken((HttpServletRequest) servletRequest);
                return;

            } else if (validationResult.getStatus() == JwtStatus.EXPIRED) {
                this.invalidTokenProcess(servletResponse, ERR_EXPIRED_TOKEN);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void authentication(JwtAuthenticationData data) {
        final Authentication authentication = new UsernamePasswordAuthenticationToken(data, "", data.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void putClaimsInServletRequest(ServletRequest servletRequest, JwtClaims claims) {
        servletRequest.setAttribute("claims", claims);
    }

    private void invalidTokenProcess(ServletResponse servletResponse, String validationMessage) throws IOException {
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        final String message = ERR_MESSAGE.formatted(System.currentTimeMillis(), validationMessage);
        response.getWriter().write(message);
    }

    private void logInvalidToken(HttpServletRequest servletRequest) {
        final String log = WARN_LOG.formatted(
                        Ipv4Util.getClientIpAddress(servletRequest),
                        servletRequest.getHeader("User-Agent"),
                        servletRequest.getRequestURI());
        logger.warn(log);
    }
}
