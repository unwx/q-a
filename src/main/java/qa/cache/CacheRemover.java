package qa.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.IUserEntityLikeSetOperation;
import qa.cache.operation.impl.*;
import qa.domain.DomainName;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Stack;

@Component
public class CacheRemover {

    private final QuestionToLikeSetOperation questionLikeOperation;
    private final AnswerToLikeSetOperation answerLikeOperation;
    private final CommentQuestionToLikeSetOperation commentQuestionLikeOperation;
    private final CommentAnswerToLikeSetOperation commentAnswerLikeOperation;

    private final UserQuestionLikeSetOperation userQuestionLikeOperation;
    private final UserAnswerLikeSetOperation userAnswerLikeOperation;
    private final UserCommentQuestionLikeSetOperation userCommentQuestionLikeOperation;
    private final UserCommentAnswerLikeSetOperation userCommentAnswerLikeOperation;

    private static final String ERR_UNKNOWN_ARGUMENT = "unknown argument: %s";
    private static final String ERR_CANNOT_DELETE = "cannot delete like cache, id: %s, names: %s";

    private static final Logger logger = LogManager.getLogger(CacheRemover.class);

    public CacheRemover(QuestionToLikeSetOperation questionLikeOperation,
                        AnswerToLikeSetOperation answerLikeOperation,
                        CommentQuestionToLikeSetOperation commentQuestionLikeOperation,
                        CommentAnswerToLikeSetOperation commentAnswerLikeOperation,
                        UserQuestionLikeSetOperation userQuestionLikeOperation,
                        UserAnswerLikeSetOperation userAnswerLikeOperation,
                        UserCommentQuestionLikeSetOperation userCommentQuestionLikeOperation,
                        UserCommentAnswerLikeSetOperation userCommentAnswerLikeOperation) {
        this.questionLikeOperation = questionLikeOperation;
        this.answerLikeOperation = answerLikeOperation;
        this.commentQuestionLikeOperation = commentQuestionLikeOperation;
        this.commentAnswerLikeOperation = commentAnswerLikeOperation;
        this.userQuestionLikeOperation = userQuestionLikeOperation;
        this.userAnswerLikeOperation = userAnswerLikeOperation;
        this.userCommentQuestionLikeOperation = userCommentQuestionLikeOperation;
        this.userCommentAnswerLikeOperation = userCommentAnswerLikeOperation;
    }

    public boolean remove(CacheOperationInstructions instructions, Jedis jedis) {
        final Map<DomainName, Stack<String>> instructionMap = instructions.getInstructions();
        for (Map.Entry<DomainName, Stack<String>> entry : instructionMap.entrySet()) {
            final DomainName name = entry.getKey();
            final Stack<String> ids = entry.getValue();
            final boolean status = this.removeIteration(name, ids, jedis);
            if (!status) return false;
        }
        return true;
    }

    public boolean remove(DomainName name, String id, Jedis jedis) {
        return this.removeIteration(name, id, jedis);
    }

    private boolean removeIteration(DomainName name, Stack<String> ids, Jedis jedis) {
        if (ids.isEmpty())
            return true;

        final CacheLikeOperation operations = resolve(name);
        final IUserEntityLikeSetOperation userEntityOperation = operations.getUserEntitySetOperation();
        final EntityToLikeSetOperation entityOperation = operations.getEntityToLikeSetOperation();

        while (ids.size() > 0) {
            final String id = ids.pop();
            final boolean status = this.deleteProcess(
                    id,
                    name,
                    userEntityOperation,
                    entityOperation,
                    jedis
            );
            if (!status) return false;
        }
        return true;
    }

    private boolean removeIteration(DomainName name, String id, Jedis jedis) {
        final CacheLikeOperation operations = resolve(name);
        final IUserEntityLikeSetOperation userEntityOperation = operations.getUserEntitySetOperation();
        final EntityToLikeSetOperation entityOperation = operations.getEntityToLikeSetOperation();

        return this.deleteProcess(id, name, userEntityOperation, entityOperation, jedis);
    }

    private boolean deleteProcess(String id,
                                        DomainName name,
                                        IUserEntityLikeSetOperation userEntityOperation,
                                        EntityToLikeSetOperation entityOperation,
                                        Jedis jedis) {

        final boolean status = entityOperation.delete(id, jedis);
        if (status) userEntityOperation.deleteEntity(id, jedis);
        else {
            logger.error(ERR_CANNOT_DELETE.formatted(id, name));
            return false;
        }
        return true;
    }

    private CacheLikeOperation resolve(DomainName name) {
        return switch (name) {
            case QUESTION -> new CacheLikeOperation(
                    this.questionLikeOperation,
                    this.userQuestionLikeOperation
            );
            case ANSWER -> new CacheLikeOperation(
                    this.answerLikeOperation,
                    this.userAnswerLikeOperation

            );
            case COMMENT_QUESTION -> new CacheLikeOperation(
                    this.commentQuestionLikeOperation,
                    this.userCommentQuestionLikeOperation
            );
            case COMMENT_ANSWER -> new CacheLikeOperation(
                    this.commentAnswerLikeOperation,
                    this.userCommentAnswerLikeOperation
            );
            default -> throw new IllegalArgumentException(
                    ERR_UNKNOWN_ARGUMENT.formatted(name)
            );
        };
    }
}
