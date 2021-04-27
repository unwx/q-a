package qa.cache.entity.like.provider.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.operation.impl.CommentAnswerToLikeSetOperation;
import qa.cache.operation.impl.UserCommentAnswerLikeSetOperation;
import redis.clients.jedis.Jedis;

@Component
public class CommentAnswerLikeProvider extends CommentLikeProvider {

    @Autowired
    public CommentAnswerLikeProvider(UserCommentAnswerLikeSetOperation userCommentOperation,
                                     CommentAnswerToLikeSetOperation commentOperation) {
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
