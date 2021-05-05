package qa.security.jwt.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import qa.dao.AuthenticationDao;
import qa.domain.AuthenticationData;
import qa.security.jwt.entity.JwtAuthenticationDataFactory;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final AuthenticationDao dao;

    private static final Logger logger = LogManager.getLogger(JwtUserDetailsService.class);
    private static final String ERR_USER_NOT_EXIST = "user %s not exist";

    @Autowired
    public JwtUserDetailsService(AuthenticationDao dao) {
        this.dao = dao;
    }

    @Override
    public UserDetails loadUserByUsername(String s) {
        final AuthenticationData data = this.dao.getAuthWithTokens(s);

        if (data == null) {
            logger.error(ERR_USER_NOT_EXIST.formatted(s));
        }

        return JwtAuthenticationDataFactory.create(data);
    }
}
