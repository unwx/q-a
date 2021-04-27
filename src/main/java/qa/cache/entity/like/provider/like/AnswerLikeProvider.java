package qa.cache.entity.like.provider.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.entity.like.provider.cache.AnswerCacheProvider;
import qa.cache.operation.impl.AnswerToLikeSetOperation;
import qa.cache.operation.impl.UserAnswerLikeSetOperation;
import qa.domain.Answer;
import redis.clients.jedis.Jedis;

import java.util.List;

@Component
public class AnswerLikeProvider extends LikesProvider {

    private final UserAnswerLikeSetOperation userAnswerOperation;
    private final AnswerToLikeSetOperation answerOperation;
    private final AnswerCacheProvider cacheProvider;

    @Autowired
    public AnswerLikeProvider(UserAnswerLikeSetOperation userAnswerOperation,
                              AnswerToLikeSetOperation answerOperation,
                              AnswerCacheProvider cacheProvider) {
        this.userAnswerOperation = userAnswerOperation;
        this.answerOperation = answerOperation;
        this.cacheProvider = cacheProvider;
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

    public void provide(List<Answer> answers,
                        long userId,
                        Jedis jedis) {

        this.cacheProvider.provide(answers, userId, jedis);
    }
}
