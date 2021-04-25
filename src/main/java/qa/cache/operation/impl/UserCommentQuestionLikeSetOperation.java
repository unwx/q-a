package qa.cache.operation.impl;

import qa.cache.RedisKeys;
import qa.cache.entity.like.set.CommentQuestionToUserLikeSet;
import qa.cache.entity.like.set.UserToCommentQuestionLikeSet;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.cache.operation.UserToEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

public class UserCommentQuestionLikeSetOperation extends UserToEntityLikeSetOperation implements IUserToEntitySetOperation<Long> {

    @Override
    public boolean add(long userId, Long commentId, Jedis jedis) {
        final UserToCommentQuestionLikeSet userToCommentSet = new UserToCommentQuestionLikeSet(userId, commentId);
        final CommentQuestionToUserLikeSet commentToUserSet = new CommentQuestionToUserLikeSet(commentId, userId);

        final boolean reply = super.add(userToCommentSet, jedis);
        if (reply) super.add(commentToUserSet, jedis);
        return reply;
    }

    @Override
    public boolean isValueExist(long userId, Long commentId, Jedis jedis) {
        final UserToCommentQuestionLikeSet set = new UserToCommentQuestionLikeSet(userId, commentId);
        return super.isValueExist(set, jedis);
    }

    @Override
    public boolean deleteEntity(Long commentId, Jedis jedis) {
        final CommentQuestionToUserLikeSet set = new CommentQuestionToUserLikeSet(commentId, -1L);
        return super.deleteLinks(
                set.getKey(),
                set.getCommentId(),
                RedisKeys.USER_COMMENT_QUESTION_LIKES,
                jedis
        );
    }
}
