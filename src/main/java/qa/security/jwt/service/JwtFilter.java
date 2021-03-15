package qa.security.jwt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import qa.exceptions.rest.ErrorMessage;
import qa.security.jwt.entity.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class JwtFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtFilter(JwtProvider jwtTokenProvider) {
        this.jwtProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String cleanToken = jwtProvider.resolveToken((HttpServletRequest) servletRequest);
        if (cleanToken != null) {
            JwtIntermediateDateTransport validationResult = jwtProvider.validate(cleanToken);
            if (validationResult.getStatus() == JwtStatus.VALID) {
                if (validationResult.getType() == JwtType.ACCESS && !((HttpServletRequest) servletRequest).getRequestURI().equals("/api/v1/authentication/refresh")) {
                    authentication(validationResult.getData());
                }
                if (validationResult.getType() == JwtType.REFRESH && ((HttpServletRequest) servletRequest).getRequestURI().equals("/api/v1/authentication/refresh-tokens")) {
                    putClaimsInServletRequest(servletRequest, validationResult.getClaims());
                }
            }
            else {
                invalidTokenProcess(servletResponse);
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

    private void invalidTokenProcess(ServletResponse servletResponse) throws IOException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String message = objectMapper.writeValueAsString(new ErrorMessage(401, new Date(), "The token is not valid.", null));
        response.getWriter().write(message);
    }
}
