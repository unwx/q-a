package qa.security.jwt.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.filter.GenericFilterBean;
import qa.security.jwt.filter.init.AuthorizedEndpointCenter;
import qa.security.jwt.filter.util.Ipv4Util;
import qa.security.jwt.service.JwtProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PreJwtFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;

    private static final Logger logger = LogManager.getLogger(PreJwtFilter.class);

    public static final String EXIST_TOKEN_ATTRIBUTE = "jwt.pre.token.existence";
    private static final String ERR_MESSAGE =
            """
            {\
            "status": 401,
            "timestamp": %s,
            "message": "token required",
            "description": null
            }\
            """;

    public PreJwtFilter(JwtProvider jwtTokenProvider) {
        this.jwtProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String cleanToken = jwtProvider.resolveToken((HttpServletRequest) request);

        boolean exist = true;
        boolean err = false;

        if (cleanToken == null) {
            exist = false;
            if (this.check(request)) {
                err = true;
                this.handle((HttpServletRequest) request, (HttpServletResponse) response);
            }
        }
        if (!err) {
            request.setAttribute(EXIST_TOKEN_ATTRIBUTE, exist);
            chain.doFilter(request, response);
        }
    }

    public boolean check(ServletRequest request) {
        final String uri = ((HttpServletRequest) request).getRequestURI();
        return AuthorizedEndpointCenter.contains(uri);
    }

    private void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.warn("trying to access %s | ipv4: %s".formatted(
                request.getRequestURI(),
                Ipv4Util.getClientIpAddress(request)
        ));

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(ERR_MESSAGE.formatted(System.currentTimeMillis()));
    }
}
