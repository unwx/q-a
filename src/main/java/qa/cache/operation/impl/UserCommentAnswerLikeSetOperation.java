package qa.cache.operation.impl;

import org.springframework.stereotype.Component;
import qa.cache.RedisKeys;
import qa.cache.entity.like.set.CommentAnswerToUserLikeSet;
import qa.cache.entity.like.set.UserToCommentAnswerLikeSet;
import qa.cache.operation.IUserEntityLikeSetOperation;
import qa.cache.operation.UserEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

@Component
public class UserCommentAnswerLikeSetOperation extends UserEntityLikeSetOperation implements IUserEntityLikeSetOperation {

    @Override
    public boolean add(String userId, String commentId, Jedis jedis) {
        final UserToCommentAnswerLikeSet userToCommentSet = new UserToCommentAnswerLikeSet(userId, commentId);
        final CommentAnswerToUserLikeSet commentToUserSet = new CommentAnswerToUserLikeSet(commentId, userId);

        final boolean reply = super.add(userToCommentSet, jedis);
        if (reply) super.add(commentToUserSet, jedis);
        return reply;
    }

    @Override
    public boolean isValueExist(String userId, String commentId, Jedis jedis) {
        final UserToCommentAnswerLikeSet set = new UserToCommentAnswerLikeSet(userId, commentId);
        return super.isValueExist(set, jedis);
    }

    @Override
    public boolean deleteEntity(String commentId, Jedis jedis) {
        final CommentAnswerToUserLikeSet set = new CommentAnswerToUserLikeSet(commentId, "-1");
        return super.deleteLinks(
                set.getKey(),
                set.getCommentId(),
                RedisKeys.USER_COMMENT_ANSWER_LIKES,
                jedis
        );
    }
}