package qa.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import qa.exceptions.rest.ErrorMessage;
import qa.security.jwt.entity.*;
import qa.security.jwt.service.JwtProvider;
import qa.util.IpUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class JwtFilter extends GenericFilterBean { // TODO REFACTOR

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LogManager.getLogger(JwtFilter.class);

    public JwtFilter(JwtProvider jwtTokenProvider) {
        this.jwtProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest.getAttribute(PreJwtFilter.EXIST_TOKEN_ATTRIBUTE).equals(true)) {
            String cleanToken = jwtProvider.resolveToken((HttpServletRequest) servletRequest);
            JwtIntermediateDateTransport validationResult = jwtProvider.validate(cleanToken);
            if (validationResult.getStatus() == JwtStatus.VALID) {
                if (validationResult.getType() == JwtType.ACCESS && !((HttpServletRequest) servletRequest).getRequestURI().equals("/api/v1/authentication/refresh")) {
                    authentication(validationResult.getData());
                }
                if (validationResult.getType() == JwtType.REFRESH && ((HttpServletRequest) servletRequest).getRequestURI().equals("/api/v1/authentication/refresh-tokens")) {
                    putClaimsInServletRequest(servletRequest, validationResult.getClaims());
                    authentication(validationResult.getData());
                }
            } else if (validationResult.getStatus() == JwtStatus.INVALID) {
                invalidTokenProcess(servletResponse, "The token is not valid.");
                logInvalidToken((HttpServletRequest) servletRequest);
                return;
            } else if (validationResult.getStatus() == JwtStatus.EXPIRED) {
                invalidTokenProcess(servletResponse, "the token is expired.");
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void authentication(JwtAuthenticationData data) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(data, "", data.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void putClaimsInServletRequest(ServletRequest servletRequest, JwtClaims claims) {
        servletRequest.setAttribute("claims", claims);
    }

    private void invalidTokenProcess(ServletResponse servletResponse, String validationMessage) throws IOException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String message = objectMapper.writeValueAsString(new ErrorMessage(401, new Date(), validationMessage, null));
        response.getWriter().write(message);
    }

    private void logInvalidToken(HttpServletRequest servletRequest) {
        String log =
                """
                        [invalid token]: the token is not valid.\s\
                        IPv4: %s\s\
                        User-Agent: %s\s\
                        URI: %s\
                        """.formatted(
                        IpUtil.getClientIpAddress(servletRequest),
                        servletRequest.getHeader("User-Agent"),
                        servletRequest.getRequestURI());
        logger.warn(log);
    }
}
