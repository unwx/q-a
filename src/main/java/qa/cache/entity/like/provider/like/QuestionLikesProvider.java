package qa.cache.entity.like.provider.like;

import org.springframework.stereotype.Component;
import qa.cache.operation.impl.QuestionToLikeSetOperation;
import qa.cache.operation.impl.UserQuestionLikeSetOperation;
import redis.clients.jedis.Jedis;

@Component
public class QuestionLikesProvider extends LikesProvider {

    private final UserQuestionLikeSetOperation userQuestionOperation;
    private final QuestionToLikeSetOperation questionOperation;

    public QuestionLikesProvider(UserQuestionLikeSetOperation userQuestionOperation,
                                 QuestionToLikeSetOperation questionOperation) {
        this.userQuestionOperation = userQuestionOperation;
        this.questionOperation = questionOperation;
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
}
