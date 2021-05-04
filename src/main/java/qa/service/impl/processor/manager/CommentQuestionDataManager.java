package qa.service.impl.processor.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import qa.dao.CommentQuestionDao;
import qa.dao.QuestionDao;
import qa.dao.database.components.Where;
import qa.dao.database.components.WhereOperator;
import qa.domain.CommentQuestion;
import qa.domain.Question;
import qa.domain.User;
import qa.dto.request.comment.CommentQuestionCreateRequest;
import qa.dto.request.comment.CommentQuestionDeleteRequest;
import qa.dto.request.comment.CommentQuestionEditRequest;
import qa.dto.response.comment.CommentQuestionResponse;
import qa.exceptions.rest.BadRequestException;
import qa.service.util.AuthorUtil;
import qa.service.util.PrincipalUtil;
import qa.service.util.ResourceUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommentQuestionDataManager {

    private final CommentQuestionDao commentQuestionDao;
    private final QuestionDao questionDao;
    private final AuthorUtil authorUtil;

    private static final String ID              = "id";
    private static final String ENTITY_NAME     = "comment";

    private static final Logger logger = LogManager.getLogger(CommentQuestionDataManager.class);
    private static final String ERR_QUESTION_NOT_EXIST = "question not exist. question id: %s";

    public CommentQuestionDataManager(CommentQuestionDao commentQuestionDao,
                                      QuestionDao questionDao,
                                      AuthorUtil authorUtil) {
        this.commentQuestionDao = commentQuestionDao;
        this.questionDao = questionDao;
        this.authorUtil = authorUtil;
    }

    public long saveNewComment(CommentQuestionCreateRequest request, Authentication authentication) {
        final CommentQuestion commentQuestion = new CommentQuestion(
                request.getText(),
                new User(PrincipalUtil.getUserIdFromAuthentication(authentication)),
                new Question(request.getQuestionId()));

        return commentQuestionDao.create(commentQuestion);
    }

    public void saveEditedComment(CommentQuestionEditRequest request) {
        final CommentQuestion commentQuestion = new CommentQuestion();
        final Where where = new Where(ID, request.getCommentId(), WhereOperator.EQUALS);

        commentQuestion.setText(request.getText());
        this.commentQuestionDao.update(where, commentQuestion);
    }

    public List<CommentQuestionResponse> getCommentsResponse(long questionId, long userId, int page) {
        final List<CommentQuestion> comments = this.getCommentFromDatabase(questionId, userId, page);
        return this.convertCommentDtoToResponse(comments);
    }

    public void deleteCommentFromDatabase(CommentQuestionDeleteRequest request) {
        final Where where = new Where(ID, request.getCommentId(), WhereOperator.EQUALS);
        this.commentQuestionDao.delete(where);
    }

    public void like(long userId, Long commentId) {
        this.commentQuestionDao.like(userId, commentId);
    }

    public void throwBadRequestExIfQuestionNotExist(long questionId) {
        if (!isQuestionExist(questionId))
            throw new BadRequestException(ERR_QUESTION_NOT_EXIST.formatted(questionId));
    }

    public void checkIsRealAuthorCommentQuestion(Long authenticationId, long commentId) {
        final Where where = new Where(ID, commentId, WhereOperator.EQUALS);
        final CommentQuestion comment = new CommentQuestion();
        this.authorUtil.checkRightsAndExistence(authenticationId, where, comment, commentQuestionDao, logger, ENTITY_NAME);
    }

    private List<CommentQuestion> getCommentFromDatabase(long questionId, long userId, int page) {
        final List<CommentQuestion> questions = this.commentQuestionDao.getComments(questionId, userId, page - 1);
        return ResourceUtil.throwResourceNFExceptionIfNull(questions, ERR_QUESTION_NOT_EXIST.formatted(questions));
    }

    private List<CommentQuestionResponse> convertCommentDtoToResponse(List<CommentQuestion> comments) {
        final List<CommentQuestionResponse> response = new ArrayList<>(comments.size());
        comments.forEach((c) -> response.add(
                new CommentQuestionResponse(
                        c.getId(),
                        c.getText(),
                        c.getCreationDate(),
                        c.getAuthor(),
                        c.getLikes(),
                        c.isLiked()
                )
        ));
        return response;
    }

    private boolean isQuestionExist(long questionId) {
        return questionDao.isExist(questionId);
    }
}
