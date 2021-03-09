package qa.security.jwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import qa.dao.AuthenticationDao;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.Table;
import qa.domain.AuthenticationData;
import qa.security.jwt.entity.JwtAuthenticationDataFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final AuthenticationDao dao = new AuthenticationDao();

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        List<String> fields = new ArrayList<>();
        fields.add("id");
        fields.add("password");
        fields.add("enabled");
        fields.add("accessTokenExpirationDateAtMills");
        fields.add("refreshTokenExpirationDateAtMillis");
        Table table = new Table(fields, "AuthenticationData");

        AuthenticationData data = dao.read(new Field("email", s), table);
        assert data != null;
        data.setEmail(s);
        return JwtAuthenticationDataFactory.create(Objects.requireNonNull(data));
    }
}
