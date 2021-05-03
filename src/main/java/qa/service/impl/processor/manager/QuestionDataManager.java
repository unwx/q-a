package qa.service.impl.processor.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import qa.dao.QuestionDao;
import qa.dao.database.components.Where;
import qa.dao.database.components.WhereOperator;
import qa.domain.Question;
import qa.domain.QuestionView;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.request.question.QuestionCreateRequest;
import qa.dto.request.question.QuestionEditRequest;
import qa.dto.response.question.QuestionFullResponse;
import qa.dto.response.question.QuestionViewResponse;
import qa.service.err.ServiceExceptionMessage;
import qa.service.util.AuthorUtil;
import qa.service.util.PrincipalUtil;
import qa.service.util.QuestionTagsUtil;
import qa.service.util.ResourceUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class QuestionDataManager {

    private final QuestionDao questionDao;
    private final PropertySetterFactory propertySetterFactory;

    private static final String ID              = "id";
    private static final String ENTITY_NAME     = "question";

    private static final Logger logger = LogManager.getLogger(QuestionDataManager.class);

    public QuestionDataManager(QuestionDao questionDao,
                               PropertySetterFactory propertySetterFactory) {
        this.questionDao = questionDao;
        this.propertySetterFactory = propertySetterFactory;
    }

    public Long saveNewQuestion(QuestionCreateRequest request, Authentication authentication) {
        final Question question = new Question.Builder()
                .creationDate(new Date())
                .lastActivity(new Date())
                .tags(QuestionTagsUtil.tagsToString(request.getTags()))
                .text(request.getText())
                .title(request.getTitle())
                .author(new User(PrincipalUtil.getUserIdFromAuthentication(authentication)))
                .build();
        return questionDao.create(question);
    }

    public void checkIsRealAuthor(long id, Authentication authentication) {
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        final Where where = new Where(ID, id, WhereOperator.EQUALS);
        final Question question = new Question();

        AuthorUtil.checkIsRealAuthorAndIsEntityExist(userId, where, question, questionDao, propertySetterFactory, logger, ENTITY_NAME);
    }

    public void saveEditedQuestion(QuestionEditRequest request) {
        final Where where = new Where(ID, request.getQuestionId(), WhereOperator.EQUALS);
        final Question question = new Question.Builder()
                .text(request.getText())
                .tags(QuestionTagsUtil.tagsToString(request.getTags()))
                .lastActivity(new Date())
                .build();

        questionDao.update(where, question);
    }

    public void deleteQuestionById(long questionId) {
        final Where where = new Where(ID, questionId, WhereOperator.EQUALS);
        this.questionDao.delete(where);
    }

    public List<QuestionViewResponse> getViewsResponse(int page) {
        final List<QuestionView> views = this.getQuestionViewsFromDatabase(page);
        return this.convertViewDtoToResponse(views);
    }

    public QuestionFullResponse getQuestionResponse(long questionId, long userId) {
        final Question question = this.getFullQuestionFromDatabase(questionId, userId);
        return this.convertDtoToResponse(question);
    }

    public void like(long userId, long questionId) {
        this.questionDao.like(userId, questionId);
    }

    private List<QuestionView> getQuestionViewsFromDatabase(int page) {
        return this.questionDao.getQuestionViewsDto(page - 1);
    }

    private Question getFullQuestionFromDatabase(long questionId, long userId) {
        final Question question = this.questionDao.getFullQuestion(questionId, userId);
        return ResourceUtil.throwResourceNFExceptionIfNull(question, ServiceExceptionMessage.ERR_MESSAGE_QUESTION_NOT_EXIST_ID.formatted(questionId));
    }

    private List<QuestionViewResponse> convertViewDtoToResponse(List<QuestionView> views) {
        final List<QuestionViewResponse> viewsResponse = new ArrayList<>(views.size());
        views.forEach((v) -> viewsResponse.add(
                new QuestionViewResponse(
                        v.getQuestionId(),
                        v.getTitle(),
                        QuestionTagsUtil.stringToTags(v.getTags()),
                        v.getCreationDate(),
                        v.getLastActivity(),
                        v.getAnswersCount(),
                        v.getAuthor())
        ));
        return viewsResponse;
    }

    private QuestionFullResponse convertDtoToResponse(Question question) {
        return new QuestionFullResponse(
                question.getId(),
                question.getText(),
                question.getTitle(),
                question.getCreationDate(),
                question.getLastActivity(),
                QuestionTagsUtil.stringToTags(question.getTags()),
                question.getAuthor(),
                question.getAnswers(),
                question.getComments(),
                question.getLikes(),
                question.isLiked()
        );
    }
}
