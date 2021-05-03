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

    private final AnswerRequestValidator validation;
    private final AnswerDataManager database;

    @Autowired
    protected AnswerServiceProcessor(AnswerRequestValidator validation,
                                     AnswerDataManager database) {
        this.validation = validation;
        this.database = database;
    }

    protected Long createAnswerProcess(AnswerCreateRequest request, Authentication authentication) {
        this.validation.validate(request);
        this.database.throwBadRequestExIfQuestionNotExist(request.getQuestionId());
        return this.database.saveNewAnswer(request, authentication);
    }

    protected void editAnswerProcess(AnswerEditRequest request, Authentication authentication) {
        this.validation.validate(request);
        this.database.checkIsRealAuthor(request.getAnswerId(), authentication);
        this.database.saveEditedAnswer(request);
    }

    protected void setAnsweredProcess(AnswerAnsweredRequest request, Authentication authentication) {
        this.validation.validate(request);
        this.database.checkIsQuestionAuthor(request.getAnswerId(), authentication);
        this.database.saveAnswered(request);
    }

    protected void removeAnsweredProcess(AnswerAnsweredRequest request, Authentication authentication) {
        this.validation.validate(request);
        this.database.checkIsQuestionAuthor(request.getAnswerId(), authentication);
        this.database.saveNotAnswered(request);
    }

    protected void deleteAnswerProcess(AnswerDeleteRequest request, Authentication authentication) {
        this.validation.validate(request);
        this.database.checkIsRealAuthor(request.getAnswerId(), authentication);
        this.database.deleteAnswerFromDatabase(request);
    }

    protected List<AnswerFullResponse> getAnswersProcess(Long questionId, Integer page, Authentication authentication) {
        return this.getAnswersProcess(new AnswerGetFullRequest(questionId, page), authentication);
    }

    protected List<AnswerFullResponse> getAnswersProcess(AnswerGetFullRequest request, Authentication authentication) {
        this.validation.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        return this.database.getAnswersResponse(request.getQuestionId(), userId, request.getPage());
    }

    protected void likeProcess(AnswerLikeRequest request, Authentication authentication) {
        this.validation.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        this.database.like(userId, request.getAnswerId());
    }
}
