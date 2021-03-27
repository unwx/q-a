package qa.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import qa.dao.UserDao;
import qa.domain.User;
import qa.dto.request.user.UserGetFullRequest;
import qa.dto.response.user.FullUserResponse;
import qa.dto.validation.wrapper.user.UserGetFullRequestValidationWrapper;
import qa.exceptions.rest.BadRequestException;
import qa.exceptions.validator.ValidationException;
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
        return new ResponseEntity<>(getFullUserProcess(username), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FullUserResponse> getFullUser(UserGetFullRequest request) {
        return new ResponseEntity<>(getFullUserProcess(request.getUsername()), HttpStatus.OK);
    }

    private FullUserResponse getFullUserProcess(String username) {
        validate(username);
        User user = getFullUserFromDatabase(username);
        return new FullUserResponse(username, user.getAbout(), user.getQuestions(), user.getAnswers());
    }

    private User getFullUserFromDatabase(String username) {
        User user = userDao.readFullUser(username);
        if (user == null)
            throw new BadRequestException("user not exist. username: " + username);
        return user;
    }

    private void validate(String username) {
        UserGetFullRequestValidationWrapper validationWrapper = new UserGetFullRequestValidationWrapper(username, validationPropertyDataSource);
        try {
            validationChain.validate(validationWrapper);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
