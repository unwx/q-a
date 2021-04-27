package qa.cache.entity.like.provider.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.operation.impl.AnswerToLikeSetOperation;
import qa.cache.operation.impl.UserAnswerLikeSetOperation;
import redis.clients.jedis.Jedis;

@Component
public class AnswerLikeProvider extends LikesProvider {

    private final UserAnswerLikeSetOperation userAnswerOperation;
    private final AnswerToLikeSetOperation answerOperation;

    @Autowired
    public AnswerLikeProvider(UserAnswerLikeSetOperation userAnswerOperation,
                              AnswerToLikeSetOperation answerOperation) {
        this.userAnswerOperation = userAnswerOperation;
        this.answerOperation = answerOperation;
    }

    public void initLike(long answerId,
                         Jedis jedis) {

        final String answerIdStr = String.valueOf(answerId);
        super.initLike(answerIdStr, answerOperation, jedis);
    }

    public void like(long userId,
                     long answerId,
                     Jedis jedis) {

        final String userIdStr = String.valueOf(userId);
        final String answerIdStr = String.valueOf(answerId);

        super.like(userIdStr, answerIdStr, userAnswerOperation, answerOperation, jedis);
    }
}
