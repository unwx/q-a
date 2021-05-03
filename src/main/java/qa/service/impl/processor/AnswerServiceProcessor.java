package qa.service.impl.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import qa.dto.request.answer.*;
import qa.dto.response.answer.AnswerFullResponse;
import qa.service.impl.processor.manager.AnswerDataManager;
import qa.service.impl.processor.validator.AnswerRequestValidator;
import qa.service.util.PrincipalUtil;

import java.util.List;

@Component
public class AnswerServiceProcessor {

    private final AnswerRequestValidator validator;
    private final AnswerDataManager dataManager;

    @Autowired
    protected AnswerServiceProcessor(AnswerRequestValidator validator,
                                     AnswerDataManager dataManager) {
        this.validator = validator;
        this.dataManager = dataManager;
    }

    protected Long createAnswerProcess(AnswerCreateRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.throwBadRequestExIfQuestionNotExist(request.getQuestionId());
        return this.dataManager.saveNewAnswer(request, authentication);
    }

    protected void editAnswerProcess(AnswerEditRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.checkIsRealAuthor(request.getAnswerId(), authentication);
        this.dataManager.saveEditedAnswer(request);
    }

    protected void setAnsweredProcess(AnswerAnsweredRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.checkIsQuestionAuthor(request.getAnswerId(), authentication);
        this.dataManager.saveAnswered(request);
    }

    protected void removeAnsweredProcess(AnswerAnsweredRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.checkIsQuestionAuthor(request.getAnswerId(), authentication);
        this.dataManager.saveNotAnswered(request);
    }

    protected void deleteAnswerProcess(AnswerDeleteRequest request, Authentication authentication) {
        this.validator.validate(request);
        this.dataManager.checkIsRealAuthor(request.getAnswerId(), authentication);
        this.dataManager.deleteAnswerFromDatabase(request);
    }

    protected List<AnswerFullResponse> getAnswersProcess(Long questionId, Integer page, Authentication authentication) {
        return this.getAnswersProcess(new AnswerGetFullRequest(questionId, page), authentication);
    }

    protected List<AnswerFullResponse> getAnswersProcess(AnswerGetFullRequest request, Authentication authentication) {
        this.validator.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        return this.dataManager.getAnswersResponse(request.getQuestionId(), userId, request.getPage());
    }

    protected void likeProcess(AnswerLikeRequest request, Authentication authentication) {
        this.validator.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        this.dataManager.like(userId, request.getAnswerId());
    }
}
