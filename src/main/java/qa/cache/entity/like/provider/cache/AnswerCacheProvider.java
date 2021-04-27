package qa.cache.entity.like.provider.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.CacheProvider;
import qa.cache.operation.impl.AnswerToLikeSetOperation;
import qa.cache.operation.impl.UserAnswerLikeSetOperation;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import redis.clients.jedis.Jedis;

import java.util.LinkedList;
import java.util.List;

@Component
public class AnswerCacheProvider extends CacheProvider {

    private final UserAnswerLikeSetOperation userAnswerOperation;
    private final AnswerToLikeSetOperation answerOperation;
    private final CommentAnswerCacheProvider commentProvider;

    @Autowired
    public AnswerCacheProvider(UserAnswerLikeSetOperation userAnswerOperation,
                               AnswerToLikeSetOperation answerOperation,
                               CommentAnswerCacheProvider commentProvider) {
        this.userAnswerOperation = userAnswerOperation;
        this.answerOperation = answerOperation;
        this.commentProvider = commentProvider;
    }

    public void provide(List<Answer> answers, long userId, Jedis jedis) {
        final String userIdStr = String.valueOf(userId);
        this.provide(answers, userIdStr, jedis);
    }

    public void provide(List<Answer> answers, String userId, Jedis jedis) {
        final List<CommentAnswer> comments = new LinkedList<>();
        answers.forEach((a) -> comments.addAll(a.getComments()));

        super.provide(answers, userId, userAnswerOperation, answerOperation, jedis);
        this.commentProvider.provide(comments, userId, jedis);
    }
}
