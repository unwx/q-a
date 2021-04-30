package qa.cache.like;

import org.springframework.stereotype.Component;
import qa.cache.like.operation.QuestionToLikeSetOperation;
import qa.cache.like.operation.UserQuestionLikeSetOperation;
import qa.cache.like.provider.QuestionCacheProvider;
import qa.cache.like.remover.QuestionCacheRemover;
import qa.domain.Question;
import qa.domain.QuestionView;
import qa.dto.internal.hibernate.question.QuestionFullStringIdsDto;
import redis.clients.jedis.Jedis;

import java.util.List;

@Component
public class QuestionLikesProvider extends LikesProvider {

    private final UserQuestionLikeSetOperation userQuestionOperation;
    private final QuestionToLikeSetOperation questionOperation;
    private final QuestionCacheProvider cacheProvider;
    private final QuestionCacheRemover cacheRemover;

    public QuestionLikesProvider(UserQuestionLikeSetOperation userQuestionOperation,
                                 QuestionToLikeSetOperation questionOperation,
                                 QuestionCacheProvider cacheProvider,
                                 QuestionCacheRemover cacheRemover) {
        this.userQuestionOperation = userQuestionOperation;
        this.questionOperation = questionOperation;
        this.cacheProvider = cacheProvider;
        this.cacheRemover = cacheRemover;
    }

    public void initLike(long questionId,
                         Jedis jedis) {

        final String questionIdStr = String.valueOf(questionId);
        super.initLike(questionIdStr, questionOperation, jedis);
    }

    public void like(long userId,
                     long questionId,
                     Jedis jedis) {

        final String userIdStr = String.valueOf(userId);
        final String questionIdStr = String.valueOf(questionId);

        super.like(userIdStr, questionIdStr, userQuestionOperation, questionOperation, jedis);
    }

    public void provide(Question question, long userId, Jedis jedis) {
        this.cacheProvider.provide(question, userId, jedis);
    }

    public void provide(List<QuestionView> views, Jedis jedis) {
        this.cacheProvider.provide(views, jedis);
    }

    public void remove(QuestionFullStringIdsDto dto, long questionId, Jedis jedis) {
        this.cacheRemover.remove(dto, questionId, jedis);
    }
}
