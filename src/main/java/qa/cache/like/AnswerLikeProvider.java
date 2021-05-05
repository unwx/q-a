package qa.cache.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.like.operation.impl.AnswerToLikeSetOperation;
import qa.cache.like.operation.impl.UserAnswerLikeSetOperation;
import qa.cache.like.provider.AnswerCacheProvider;
import qa.cache.like.remover.AnswerCacheRemover;
import qa.domain.Answer;
import qa.dto.internal.hibernate.entities.answer.AnswerFullStringIdsDto;
import redis.clients.jedis.Jedis;

import java.util.List;

@Component
public class AnswerLikeProvider extends LikesProvider {

    private final UserAnswerLikeSetOperation userAnswerOperation;
    private final AnswerToLikeSetOperation answerOperation;
    private final AnswerCacheProvider cacheProvider;
    private final AnswerCacheRemover cacheRemover;

    @Autowired
    public AnswerLikeProvider(UserAnswerLikeSetOperation userAnswerOperation,
                              AnswerToLikeSetOperation answerOperation,
                              AnswerCacheProvider cacheProvider,
                              AnswerCacheRemover cacheRemover) {
        this.userAnswerOperation = userAnswerOperation;
        this.answerOperation = answerOperation;
        this.cacheProvider = cacheProvider;
        this.cacheRemover = cacheRemover;
    }

    public void initLike(long answerId, Jedis jedis) {
        final String answerIdStr = String.valueOf(answerId);
        super.initLike(answerIdStr, answerOperation, jedis);
    }

    public void like(long userId, long answerId, Jedis jedis) {
        final String userIdStr = String.valueOf(userId);
        final String answerIdStr = String.valueOf(answerId);

        super.like(userIdStr, answerIdStr, userAnswerOperation, answerOperation, jedis);
    }

    public void provide(List<Answer> answers, long userId, Jedis jedis) {
        this.cacheProvider.provide(answers, userId, jedis);
    }

    public void remove(AnswerFullStringIdsDto dto, long answerId, Jedis jedis) {
        this.cacheRemover.remove(dto, answerId, jedis);
    }
}
