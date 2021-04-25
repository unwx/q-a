package qa.cache.operation.impl;

import qa.cache.RedisKeys;
import qa.cache.entity.like.set.CommentAnswerToUserLikeSet;
import qa.cache.entity.like.set.UserToCommentAnswerLikeSet;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.cache.operation.UserToEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

public class UserCommentAnswerLikeSetOperation extends UserToEntityLikeSetOperation implements IUserToEntitySetOperation<Long> {

    @Override
    public boolean add(long userId, Long commentId, Jedis jedis) {
        final UserToCommentAnswerLikeSet userToCommentSet = new UserToCommentAnswerLikeSet(userId, commentId);
        final CommentAnswerToUserLikeSet commentToUserSet = new CommentAnswerToUserLikeSet(commentId, userId);

        final boolean reply = super.add(userToCommentSet, jedis);
        if (reply) super.add(commentToUserSet, jedis);
        return reply;
    }

    @Override
    public boolean isValueExist(long userId, Long commentId, Jedis jedis) {
        final UserToCommentAnswerLikeSet set = new UserToCommentAnswerLikeSet(userId, commentId);
        return super.isValueExist(set, jedis);
    }

    @Override
    public boolean deleteValue(long userId, Long commentId, Jedis jedis) {
        final UserToCommentAnswerLikeSet set = new UserToCommentAnswerLikeSet(userId, commentId);
        return super.deleteValue(set, jedis);
    }

    @Override
    public boolean deleteKey(long userId, Jedis jedis) {
        final UserToCommentAnswerLikeSet set = new UserToCommentAnswerLikeSet(userId, -1L);
        return super.deleteKey(set, jedis);
    }

    @Override
    public boolean deleteEntity(Long commentId, Jedis jedis) {
        final CommentAnswerToUserLikeSet set = new CommentAnswerToUserLikeSet(commentId, -1L);
        return super.deleteLinks(
                set.getKey(),
                set.getCommentId(),
                RedisKeys.USER_COMMENT_ANSWER_LIKES,
                jedis
        );
    }
}