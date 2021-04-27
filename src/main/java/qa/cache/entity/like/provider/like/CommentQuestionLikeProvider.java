package qa.cache.entity.like.provider.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.operation.impl.CommentQuestionToLikeSetOperation;
import qa.cache.operation.impl.UserCommentQuestionLikeSetOperation;
import redis.clients.jedis.Jedis;

@Component
public class CommentQuestionLikeProvider extends CommentLikeProvider {

    @Autowired
    public CommentQuestionLikeProvider(UserCommentQuestionLikeSetOperation userCommentOperation,
                                       CommentQuestionToLikeSetOperation commentOperation) {
        super(userCommentOperation, commentOperation);
    }

    public void initLike(long commentId,
                         Jedis jedis) {

        super.initLike(commentId, jedis);
    }

    public void like(long userId,
                     long commentId,
                     Jedis jedis) {

        super.like(userId, commentId, jedis);
    }
}
