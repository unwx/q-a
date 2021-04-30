package qa.security.jwt.service;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import qa.security.jwt.filter.JwtFilter;
import qa.security.jwt.filter.PreJwtFilter;

public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtProvider jwtProvider;

    public JwtConfigurer(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void configure(HttpSecurity http) {
        final JwtFilter jwtFilter = new JwtFilter(jwtProvider);
        final PreJwtFilter preJwtFilter = new PreJwtFilter(jwtProvider); // TODO RENAME
        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(preJwtFilter, JwtFilter.class);
    }
}