package qa.cache.operation.impl;

import org.springframework.stereotype.Component;
import qa.cache.RedisKeys;
import qa.cache.entity.like.set.CommentQuestionToUserLikeSet;
import qa.cache.entity.like.set.UserToCommentQuestionLikeSet;
import qa.cache.operation.IUserCommentLikeSetOperation;
import qa.cache.operation.UserEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

@Component
public class UserCommentQuestionLikeSetOperation extends UserEntityLikeSetOperation implements IUserCommentLikeSetOperation {

    @Override
    public boolean add(String userId, String commentId, Jedis jedis) {
        final UserToCommentQuestionLikeSet userToCommentSet = new UserToCommentQuestionLikeSet(userId, commentId);
        final CommentQuestionToUserLikeSet commentToUserSet = new CommentQuestionToUserLikeSet(commentId, userId);

        final boolean reply = super.add(userToCommentSet, jedis);
        if (reply) super.add(commentToUserSet, jedis);
        return reply;
    }

    @Override
    public boolean isValueExist(String userId, String commentId, Jedis jedis) {
        final UserToCommentQuestionLikeSet set = new UserToCommentQuestionLikeSet(userId, commentId);
        return super.isValueExist(set, jedis);
    }

    @Override
    public boolean deleteEntity(String commentId, Jedis jedis) {
        final CommentQuestionToUserLikeSet set = new CommentQuestionToUserLikeSet(commentId, "-1");
        return super.deleteLinks(
                set.getKey(),
                set.getCommentId(),
                RedisKeys.USER_COMMENT_QUESTION_LIKES,
                jedis
        );
    }
}
