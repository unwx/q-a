package qa.service.impl.aid.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import qa.dto.request.question.*;
import qa.dto.response.question.QuestionFullResponse;
import qa.dto.response.question.QuestionViewResponse;
import qa.service.impl.aid.process.database.QuestionServiceDatabase;
import qa.service.impl.aid.process.validation.QuestionServiceValidation;
import qa.service.util.PrincipalUtil;

import java.util.List;

@Component
public class QuestionServiceProcess {

    private final QuestionServiceValidation validation;
    private final QuestionServiceDatabase database;

    @Autowired
    protected QuestionServiceProcess(QuestionServiceValidation validation,
                                  QuestionServiceDatabase database) {
        this.validation = validation;
        this.database = database;
    }

    protected Long createQuestionProcess(QuestionCreateRequest request, Authentication authentication) {
        this.validation.validate(request);
        return this.database.saveNewQuestion(request, authentication);
    }

    protected void editQuestionProcess(QuestionEditRequest request, Authentication authentication) {
        this.validation.validate(request);
        this.database.checkIsRealAuthor(request.getQuestionId(), authentication);
        this.database.saveEditedQuestion(request);
    }

    protected void deleteQuestionProcess(QuestionDeleteRequest request, Authentication authentication) {
        this.validation.validate(request);
        this.database.checkIsRealAuthor(request.getQuestionId(), authentication);
        this.database.deleteQuestionById(request.getQuestionId());
    }

    protected List<QuestionViewResponse> getQuestionsProcess(Integer page) {
        return this.getQuestionsProcess(new QuestionGetViewsRequest(page));
    }

    protected List<QuestionViewResponse> getQuestionsProcess(QuestionGetViewsRequest request) {
        this.validation.validate(request);
        return this.database.getViewsResponse(request.getPage());
    }

    protected QuestionFullResponse getFullQuestionProcess(Long questionId, Authentication authentication) {
        return this.getFullQuestionProcess(new QuestionGetFullRequest(questionId), authentication);
    }

    protected QuestionFullResponse getFullQuestionProcess(QuestionGetFullRequest request, Authentication authentication) {
        this.validation.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        return this.database.getQuestionResponse(request.getQuestionId(), userId);
    }

    protected void likeProcess(QuestionLikeRequest request, Authentication authentication) {
        this.validation.validate(request);
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        this.database.like(userId, request.getQuestionId());
    }
}
