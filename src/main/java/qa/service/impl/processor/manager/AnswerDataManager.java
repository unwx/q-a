package qa.service.impl.processor.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import qa.dao.AnswerDao;
import qa.dao.QuestionDao;
import qa.dao.database.components.Where;
import qa.dao.database.components.WhereOperator;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.dto.request.answer.AnswerAnsweredRequest;
import qa.dto.request.answer.AnswerCreateRequest;
import qa.dto.request.answer.AnswerDeleteRequest;
import qa.dto.request.answer.AnswerEditRequest;
import qa.dto.response.answer.AnswerFullResponse;
import qa.exceptions.rest.AccessDeniedException;
import qa.exceptions.rest.BadRequestException;
import qa.service.err.ServiceExceptionMessage;
import qa.service.util.AuthorUtil;
import qa.service.util.PrincipalUtil;
import qa.service.util.ResourceUtil;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AnswerDataManager {

    private final AnswerDao answerDao;
    private final QuestionDao questionDao;
    private final AuthorUtil authorUtil;

    private static final String ID              = "id";
    private static final String ENTITY_NAME     = "answer";

    private static final Logger logger = LogManager.getLogger(AnswerFullResponse.class);

    private static final String ERR_QUESTION_NOT_EXIST  = "question not exist. id: %s";
    private static final String ERR_ACCESS_DENIED       = "you do not have permission to this answer";

    public AnswerDataManager(AnswerDao answerDao,
                             QuestionDao questionDao,
                             AuthorUtil authorUtil) {
        this.answerDao = answerDao;
        this.questionDao = questionDao;
        this.authorUtil = authorUtil;
    }

    public Long saveNewAnswer(AnswerCreateRequest request, Authentication authentication) {
        final Answer answer = new Answer.Builder()
                .text(request.getText())
                .answered(false)
                .creationDate(new Date())
                .author(new User(PrincipalUtil.getUserIdFromAuthentication(authentication)))
                .question(new Question(request.getQuestionId()))
                .build();
        return this.answerDao.create(answer);
    }

    public void saveEditedAnswer(AnswerEditRequest request) {
        final Where where = new Where(ID, request.getAnswerId(), WhereOperator.EQUALS);
        final Answer answer = new Answer.Builder()
                .text(request.getText())
                .build();

        this.answerDao.update(where, answer);
    }

    public void saveAnswered(AnswerAnsweredRequest request) {
        final Where where = new Where(ID, request.getAnswerId(), WhereOperator.EQUALS);
        final Answer answer = new Answer.Builder().answered(true).build();

        this.answerDao.update(where, answer);
    }

    public void saveNotAnswered(AnswerAnsweredRequest request) {
        final Where where = new Where(ID, request.getAnswerId(), WhereOperator.EQUALS);
        final Answer answer = new Answer.Builder().answered(false).build();

        this.answerDao.update(where, answer);
    }

    public void deleteAnswerFromDatabase(AnswerDeleteRequest request) {
        final Where where = new Where(ID, request.getAnswerId(), WhereOperator.EQUALS);
        this.answerDao.delete(where);
    }

    public List<AnswerFullResponse> getAnswersResponse(long questionId, long userId, int page) {
        final List<Answer> answers = this.getAnswersFromDatabase(questionId, userId, page);
        return this.convertToResponse(answers);
    }

    public void like(long userId, long answerId) {
        this.answerDao.like(userId, answerId);
    }

    /**
     *
     * @throws
     * qa.exceptions.rest.ResourceNotFoundException:
     * if answer not exist
     *
     * AuthorNotExistException:
     * if author not exist
     *
     * AccessDeniedException:
     * if not real author
     */
    public void checkIsRealAuthor(Long answerId, Authentication authentication) {
        final long userId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        final Where where = new Where(ID, answerId, WhereOperator.EQUALS);
        final Answer answer = new Answer();

        this.authorUtil.checkRightsAndExistence(userId, where, answer, answerDao, logger, ENTITY_NAME);
    }

    public void checkIsQuestionAuthor(long answerId, Authentication authentication) {
        final long realAuthorId = this.questionDao.getQuestionAuthorIdFromAnswer(answerId);
        final long authenticationId = PrincipalUtil.getUserIdFromAuthentication(authentication);
        if (realAuthorId != authenticationId) throw new AccessDeniedException(ERR_ACCESS_DENIED);
    }

    /**
     *
     * @throws BadRequestException:
     * if question not exist
     */
    public void throwBadRequestExIfQuestionNotExist(long questionId) {
        if (!isQuestionExist(questionId)) throw new BadRequestException(ERR_QUESTION_NOT_EXIST.formatted(questionId));
    }

    /**
     *
     * @throws qa.exceptions.rest.ResourceNotFoundException:
     * if result is null
     */
    private List<Answer> getAnswersFromDatabase(long questionId, long userId, int page) {
        final List<Answer> answers = this.answerDao.getAnswers(questionId, userId, page - 1);
        return ResourceUtil.throwResourceNFExceptionIfNull(answers, ServiceExceptionMessage.ERR_MESSAGE_QUESTION_NOT_EXIST_ID.formatted(questionId));
    }

    private List<AnswerFullResponse> convertToResponse(List<Answer> answers) {
        return answers.stream().map((a) -> new AnswerFullResponse(
                a.getId(),
                a.getText(),
                a.getCreationDate(),
                a.getAnswered(),
                a.getAuthor(),
                a.getComments(),
                a.getLikes(),
                a.isLiked()
        )).collect(Collectors.toList());
    }

    private boolean isQuestionExist(long questionId) {
        return this.questionDao.isExist(questionId);
    }
}
