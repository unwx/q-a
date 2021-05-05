package qa.cache.like.remover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.like.operation.impl.QuestionToLikeSetOperation;
import qa.cache.like.operation.impl.UserQuestionLikeSetOperation;
import qa.dto.internal.hibernate.entities.question.QuestionFullStringIdsDto;
import redis.clients.jedis.Jedis;

import java.util.Stack;

@Component
public class QuestionCacheRemover extends EntityCacheRemover {

    private final AnswerCacheRemover answerCacheRemover;
    private final CommentQuestionCacheRemover commentQuestionCacheRemover;
    private final CommentAnswerCacheRemover commentAnswerCacheRemover;

    @Autowired
    public QuestionCacheRemover(UserQuestionLikeSetOperation userEntityOperation,
                                QuestionToLikeSetOperation entityOperation,
                                AnswerCacheRemover answerCacheRemover,
                                CommentQuestionCacheRemover commentQuestionCacheRemover,
                                CommentAnswerCacheRemover commentAnswerCacheRemover) {
        super(userEntityOperation, entityOperation);
        this.answerCacheRemover = answerCacheRemover;
        this.commentQuestionCacheRemover = commentQuestionCacheRemover;
        this.commentAnswerCacheRemover = commentAnswerCacheRemover;
    }

    public void remove(QuestionFullStringIdsDto dto, long questionId, Jedis jedis) {
        final String questionIdStr = String.valueOf(questionId);
        final Stack<String> answerIds = dto.getAnswerIds();
        final Stack<String> commentQuestionIds = dto.getCommentQuestionIds();
        final Stack<String> commentAnswerIds = dto.getCommentAnswerIds();

        super.removeEntity(questionIdStr, jedis);
        this.answerCacheRemover.remove(answerIds, jedis);
        this.commentQuestionCacheRemover.remove(commentQuestionIds, jedis);
        this.commentAnswerCacheRemover.remove(commentAnswerIds, jedis);
    }
}
