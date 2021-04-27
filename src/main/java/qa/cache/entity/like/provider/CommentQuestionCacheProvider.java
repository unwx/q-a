package qa.cache.entity.like.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.CacheProvider;
import qa.cache.operation.impl.CommentQuestionToLikeSetOperation;
import qa.cache.operation.impl.UserCommentQuestionLikeSetOperation;
import qa.domain.CommentQuestion;
import redis.clients.jedis.Jedis;

import java.util.List;

@Component
public class CommentQuestionCacheProvider extends CacheProvider {

    private final UserCommentQuestionLikeSetOperation userCommentOperation;
    private final CommentQuestionToLikeSetOperation commentOperation;

    @Autowired
    public CommentQuestionCacheProvider(UserCommentQuestionLikeSetOperation userCommentOperation,
                                        CommentQuestionToLikeSetOperation commentOperation) {
        this.userCommentOperation = userCommentOperation;
        this.commentOperation = commentOperation;
    }

    public void provide(List<CommentQuestion> comments, long userId, Jedis jedis) {
        final String userIdStr = String.valueOf(userId);
        super.provide(comments, userIdStr, userCommentOperation, commentOperation, jedis);
    }

    public void provide(List<CommentQuestion> comments, String userId, Jedis jedis) {
        super.provide(comments, userId, userCommentOperation, commentOperation, jedis);
    }
}
