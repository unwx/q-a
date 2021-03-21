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

    private final static Logger logger = LogManager.getLogger(JwtUserDetailsService.class);

    @Autowired
    public JwtUserDetailsService(AuthenticationDao dao) {
        this.dao = dao;
    }

    @Override
    public UserDetails loadUserByUsername(String s) {
        AuthenticationData data = dao.getPrincipalWithTokenData(s);
        if (data == null) {
            logger.error("user " + s + " not exist. throw UserNotFoundException");
        }
        return JwtAuthenticationDataFactory.create(data);
    }
}
