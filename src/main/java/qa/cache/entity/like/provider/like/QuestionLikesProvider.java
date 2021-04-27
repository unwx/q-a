package qa.cache.entity.like.provider.like;

import org.springframework.stereotype.Component;
import qa.cache.entity.like.provider.cache.QuestionCacheProvider;
import qa.cache.operation.impl.QuestionToLikeSetOperation;
import qa.cache.operation.impl.UserQuestionLikeSetOperation;
import qa.domain.Question;
import qa.domain.QuestionView;
import redis.clients.jedis.Jedis;

import java.util.List;

@Component
public class QuestionLikesProvider extends LikesProvider {

    private final UserQuestionLikeSetOperation userQuestionOperation;
    private final QuestionToLikeSetOperation questionOperation;
    private final QuestionCacheProvider cacheProvider;

    public QuestionLikesProvider(UserQuestionLikeSetOperation userQuestionOperation,
                                 QuestionToLikeSetOperation questionOperation,
                                 QuestionCacheProvider cacheProvider) {
        this.userQuestionOperation = userQuestionOperation;
        this.questionOperation = questionOperation;
        this.cacheProvider = cacheProvider;
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
}
