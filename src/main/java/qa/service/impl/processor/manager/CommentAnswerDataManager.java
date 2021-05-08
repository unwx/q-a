package qa.service.impl.processor.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import qa.dao.AnswerDao;
import qa.dao.CommentAnswerDao;
import qa.dao.database.components.Where;
import qa.dao.database.components.WhereOperator;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.User;
import qa.dto.request.comment.CommentAnswerCreateRequest;
import qa.dto.request.comment.CommentAnswerDeleteRequest;
import qa.dto.request.comment.CommentAnswerEditRequest;
import qa.dto.response.comment.CommentAnswerResponse;
import qa.exceptions.rest.BadRequestException;
import qa.service.util.AuthorUtil;
import qa.service.util.PrincipalUtil;
import qa.service.util.ResourceUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommentAnswerDataManager {

    private final AnswerDao answerDao;
    private final CommentAnswerDao commentAnswerDao;
    private final AuthorUtil authorUtil;

    private static final String ID              = "id";
    private static final String ENTITY_NAME     = "comment";

    private static final Logger logger = LogManager.getLogger(CommentAnswerDataManager.class);
    private static final String ERR_ANSWER_NOT_EXIST = "answer not exist. id: %s";

    @Autowired
    public CommentAnswerDataManager(AnswerDao answerDao,
                                    CommentAnswerDao commentAnswerDao,
                                    AuthorUtil authorUtil) {
        this.answerDao = answerDao;
        this.commentAnswerDao = commentAnswerDao;
        this.authorUtil = authorUtil;
    }

    public Long saveNewComment(CommentAnswerCreateRequest request, Authentication authentication) {
        final CommentAnswer commentAnswer = new CommentAnswer(
                request.getText(),
                new User(PrincipalUtil.getUserIdFromAuthentication(authentication)),
                new Answer(request.getAnswerId()));

        return this.commentAnswerDao.create(commentAnswer);
    }

    public void saveEditedComment(CommentAnswerEditRequest request) {
        final Where where = new Where(ID, request.getCommentId(), WhereOperator.EQUALS);
        final CommentAnswer commentAnswer = new CommentAnswer();

        commentAnswer.setText(request.getText());
        this.commentAnswerDao.update(where, commentAnswer);
    }

    public void deleteCommentFromDatabase(CommentAnswerDeleteRequest request) {
        final Where where = new Where(ID, request.getCommentId(), WhereOperator.EQUALS);
        this.commentAnswerDao.delete(where);
    }

    public List<CommentAnswerResponse> getCommentsResponse(long commentId, long userId, int page) {
        final List<CommentAnswer> comments = this.getCommentsFromDatabase(commentId, userId, page);
        return this.convertDtoToResponse(comments);
    }

    public void like(long userId, long commentId) {
        this.commentAnswerDao.like(userId, commentId);
    }

    /**
     *
     * @throws BadRequestException:
     * if answer not exist
     */
    public void throwBadRequestExIfAnswerNotExist(long commentId) {
        if (!isAnswerExist(commentId)) throw new BadRequestException(ERR_ANSWER_NOT_EXIST.formatted(commentId));
    }

    /**
     *
     * @throws
     * qa.exceptions.rest.ResourceNotFoundException:
     * if comment not exist
     *
     * AuthorNotExistException:
     * if author not exist
     *
     * AccessDeniedException:
     * if not real author
     */
    public void checkIsRealAuthor(Long authenticationId, long commentId) {
        final Where where = new Where(ID, commentId, WhereOperator.EQUALS);
        final CommentAnswer comment = new CommentAnswer();
        this.authorUtil.checkRightsAndExistence(authenticationId, where, comment, commentAnswerDao, logger, ENTITY_NAME);
    }

    /**
     *
     * @throws qa.exceptions.rest.ResourceNotFoundException:
     * if result is null
     */
    private List<CommentAnswer> getCommentsFromDatabase(long commentId, long userId, int page) {
        final List<CommentAnswer> comments = this.commentAnswerDao.getComments(commentId, userId, page - 1);
        return ResourceUtil.throwResourceNFExceptionIfNull(comments, ERR_ANSWER_NOT_EXIST.formatted(commentId));
    }

    private List<CommentAnswerResponse> convertDtoToResponse(List<CommentAnswer> dto) {
        final List<CommentAnswerResponse> response = new ArrayList<>(dto.size());
        dto.forEach((d) -> response.add(
                new CommentAnswerResponse(
                        d.getId(),
                        d.getText(),
                        d.getCreationDate(),
                        d.getAuthor(),
                        d.getLikes(),
                        d.isLiked()
                )
        ));
        return response;
    }

    private boolean isAnswerExist(long answerId) {
        return this.answerDao.isExist(answerId);
    }
}
