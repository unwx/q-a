package qa.security.jwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import qa.dao.AuthenticationDao;
import qa.domain.AuthenticationData;
import qa.security.jwt.entity.JwtAuthenticationDataFactory;

import java.util.Objects;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final AuthenticationDao dao = new AuthenticationDao();

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthenticationData data = dao.getPrincipalWithTokenData(s);
        assert data != null;
        return JwtAuthenticationDataFactory.create(Objects.requireNonNull(data));
    }
}
