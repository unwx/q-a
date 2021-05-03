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

    private final UserRequestValidator validator;
    private final UserDataManager dataManager;

    protected UserServiceProcessor(UserRequestValidator validator,
                                   UserDataManager dataManager) {
        this.validator = validator;
        this.dataManager = dataManager;
    }

    protected UserFullResponse getFullUserProcess(String username) {
        return this.getFullUserProcess(new UserGetFullRequest(username));
    }

    protected UserFullResponse getFullUserProcess(UserGetFullRequest request) {
        this.validator.validate(request);
        return this.dataManager.getUserResponse(request.getUsername());
    }

    protected List<UserQuestionsResponse> getUserQuestionsProcess(Long userId, Integer page) {
        return this.getUserQuestionsProcess(new UserGetQuestionsRequest(userId, page));
    }

    protected List<UserQuestionsResponse> getUserQuestionsProcess(UserGetQuestionsRequest request) {
        this.validator.validate(request);
        return this.dataManager.getUserQuestionsResponse(request.getUserId(), request.getPage());
    }

    protected List<UserAnswersResponse> getUserAnswersProcess(Long userId, Integer page) {
        return this.getUserAnswersProcess(new UserGetAnswersRequest(userId, page));
    }

    protected List<UserAnswersResponse> getUserAnswersProcess(UserGetAnswersRequest request) {
        this.validator.validate(request);
        return this.dataManager.getUserAnswersResponse(request.getUserId(), request.getPage());
    }
}
