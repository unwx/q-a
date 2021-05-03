package qa.service.impl.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import qa.dto.request.question.*;
import qa.dto.response.question.QuestionFullResponse;
import qa.dto.response.question.QuestionViewResponse;
import qa.service.impl.processor.manager.QuestionDataManager;
import qa.service.impl.processor.validator.QuestionRequestValidator;
import qa.service.util.PrincipalUtil;

import java.util.List;

@Component
public class QuestionServiceProcessor {

    private final QuestionRequestValidator validator;
    private final QuestionDataManager dataManager;

    @Autowired
    protected QuestionServiceProcessor(QuestionRequestValidator validator,
                                       QuestionDataManager dataManager) {
        this.validator = validator;
        this.dataManager = dataManager;
    }

    protected Long createQuestionProcess(QuestionCreateRequest request, Authentication authentication) {
        this.validator.validate(request);
        return this.dataManager.saveNewQuestion(request, authentication);
    }

    protected void editQuestionProcess(QuestionEditRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.checkIsRealAuthor(request.getQuestionId(), authentication);
        this.dataManager.saveEditedQuestion(request);
    }

    protected void deleteQuestionProcess(QuestionDeleteRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.checkIsRealAuthor(request.getQuestionId(), authentication);
        this.dataManager.deleteQuestionById(request.getQuestionId());
    }

    protected List<QuestionViewResponse> getQuestionsProcess(Integer page) {
        return this.getQuestionsProcess(new QuestionGetViewsRequest(page));
    }

    protected List<QuestionViewResponse> getQuestionsProcess(QuestionGetViewsRequest request) {
        this.validator.validate(request);
        return this.dataManager.getViewsResponse(request.getPage());
    }

    protected QuestionFullResponse getFullQuestionProcess(Long questionId, Authentication authentication) {
        return this.getFullQuestionProcess(new QuestionGetFullRequest(questionId), authentication);
    }

    protected QuestionFullResponse getFullQuestionProcess(QuestionGetFullRequest request, Authentication authentication) {
        this.validator.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        return this.dataManager.getQuestionResponse(request.getQuestionId(), userId);
    }

    protected void likeProcess(QuestionLikeRequest request, Authentication authentication) {
        this.validator.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        this.dataManager.like(userId, request.getQuestionId());
    }
}
