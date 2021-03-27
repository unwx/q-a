package qa.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import qa.dao.UserDao;
import qa.dto.request.user.UserGetFullRequest;
import qa.dto.response.user.FullUserResponse;
import qa.service.UserService;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationChainAdditional;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final ValidationChainAdditional validationChain;
    private final ValidationPropertyDataSource validationPropertyDataSource;

    public UserServiceImpl(UserDao userDao,
                           ValidationChainAdditional validationChain,
                           ValidationPropertyDataSource validationPropertyDataSource) {
        this.userDao = userDao;
        this.validationChain = validationChain;
        this.validationPropertyDataSource = validationPropertyDataSource;
    }

    @Override
    public ResponseEntity<FullUserResponse> getFullUser(String username) {
        return null;
    }

    @Override
    public ResponseEntity<FullUserResponse> getFullUser(UserGetFullRequest request) {
        return null;
    }
}
