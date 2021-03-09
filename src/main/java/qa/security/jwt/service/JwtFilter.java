package qa.security.jwt.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import qa.security.jwt.entity.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtTokenProvider) {
        this.jwtProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String cleanToken = jwtProvider.resolveToken((HttpServletRequest) servletRequest);
        if (cleanToken != null) {
            JwtIntermediateDateTransport validationResult = jwtProvider.validate(cleanToken);
            if (validationResult.getStatus() == JwtStatus.VALID) {
                if (validationResult.getType() == JwtType.ACCESS && !((HttpServletRequest) servletRequest).getRequestURI().equals("/api/v1/auth/refresh")) {
                    authentication(validationResult.getData());
                }
                if (validationResult.getType() == JwtType.REFRESH && ((HttpServletRequest) servletRequest).getRequestURI().equals("/api/v1/auth/refresh")) {
                    putClaimsInServletRequest(servletRequest, validationResult.getClaims());
                }
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
}
