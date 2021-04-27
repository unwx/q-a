package qa.cache.entity.like.provider;

import org.springframework.stereotype.Component;
import qa.cache.CacheProvider;
import qa.cache.operation.impl.CommentAnswerToLikeSetOperation;
import qa.cache.operation.impl.UserCommentAnswerLikeSetOperation;
import qa.domain.CommentAnswer;
import redis.clients.jedis.Jedis;

import java.util.List;

@Component
public class CommentAnswerCacheProvider extends CacheProvider {

    private final UserCommentAnswerLikeSetOperation userCommentOperation;
    private final CommentAnswerToLikeSetOperation commentOperation;

    public CommentAnswerCacheProvider(UserCommentAnswerLikeSetOperation userCommentOperation,
                                      CommentAnswerToLikeSetOperation commentOperation) {
        this.userCommentOperation = userCommentOperation;
        this.commentOperation = commentOperation;
    }

    public void provide(List<CommentAnswer> comments, long userId, Jedis jedis) {
        final String userIdStr = String.valueOf(userId);
        super.provide(comments, userIdStr, userCommentOperation, commentOperation, jedis);
    }

    public void provide(List<CommentAnswer> comments, String userId, Jedis jedis) {
        super.provide(comments, userId, userCommentOperation, commentOperation, jedis);
    }
}
