package qa.cache.entity.like.provider.remover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.operation.impl.AnswerToLikeSetOperation;
import qa.cache.operation.impl.UserAnswerLikeSetOperation;
import qa.dto.internal.hibernate.answer.AnswerFullStringIdsDto;
import redis.clients.jedis.Jedis;

import java.util.Stack;

@Component
public class AnswerCacheRemover extends EntityCacheRemover {

    private final CommentAnswerCacheRemover cacheRemover;

    @Autowired
    public AnswerCacheRemover(UserAnswerLikeSetOperation userAnswerOperation,
                              AnswerToLikeSetOperation answerOperation,
                              CommentAnswerCacheRemover cacheRemover) {
        super(userAnswerOperation, answerOperation);
        this.cacheRemover = cacheRemover;
    }

    public void remove(AnswerFullStringIdsDto dto, long answerId, Jedis jedis) {
        final String answerIdStr = String.valueOf(answerId);
        final Stack<String> commentIds = dto.getCommentAnswerIds();
        super.removeEntity(answerIdStr, jedis);
        this.cacheRemover.remove(commentIds, jedis);
    }

    public void remove(Stack<String> answerIds, Jedis jedis) {
        super.removeEntities(answerIds, jedis);
    }
}
