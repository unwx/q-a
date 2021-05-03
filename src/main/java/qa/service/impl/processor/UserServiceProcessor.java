package qa.service.impl.processor;

import org.springframework.stereotype.Component;
import qa.dto.request.user.UserGetAnswersRequest;
import qa.dto.request.user.UserGetFullRequest;
import qa.dto.request.user.UserGetQuestionsRequest;
import qa.dto.response.user.UserAnswersResponse;
import qa.dto.response.user.UserFullResponse;
import qa.dto.response.user.UserQuestionsResponse;
import qa.service.impl.processor.manager.UserDataManager;
import qa.service.impl.processor.validator.UserRequestValidator;

import java.util.List;

@Component
public class UserServiceProcessor {

    private final UserRequestValidator validation;
    private final UserDataManager database;

    protected UserServiceProcessor(UserRequestValidator validation,
                                   UserDataManager database) {
        this.validation = validation;
        this.database = database;
    }

    protected UserFullResponse getFullUserProcess(String username) {
        return this.getFullUserProcess(new UserGetFullRequest(username));
    }

    protected UserFullResponse getFullUserProcess(UserGetFullRequest request) {
        this.validation.validate(request);
        return this.database.getUserResponse(request.getUsername());
    }

    protected List<UserQuestionsResponse> getUserQuestionsProcess(Long userId, Integer page) {
        return this.getUserQuestionsProcess(new UserGetQuestionsRequest(userId, page));
    }

    protected List<UserQuestionsResponse> getUserQuestionsProcess(UserGetQuestionsRequest request) {
        this.validation.validate(request);
        return this.database.getUserQuestionsResponse(request.getUserId(), request.getPage());
    }

    protected List<UserAnswersResponse> getUserAnswersProcess(Long userId, Integer page) {
        return this.getUserAnswersProcess(new UserGetAnswersRequest(userId, page));
    }

    protected List<UserAnswersResponse> getUserAnswersProcess(UserGetAnswersRequest request) {
        this.validation.validate(request);
        return this.database.getUserAnswersResponse(request.getUserId(), request.getPage());
    }
}
