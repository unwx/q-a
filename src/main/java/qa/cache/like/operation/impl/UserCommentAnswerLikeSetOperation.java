package qa.cache.like.operation.impl;

import org.springframework.stereotype.Component;
import qa.cache.RedisKeys;
import qa.cache.abstraction.UserEntitySetOperation;
import qa.cache.like.entity.CommentAnswerToUserLikeSet;
import qa.cache.like.entity.UserToCommentAnswerLikeSet;
import qa.cache.like.operation.IUserCommentLikeSetOperation;
import redis.clients.jedis.Jedis;

@Component
public class UserCommentAnswerLikeSetOperation extends UserEntitySetOperation implements IUserCommentLikeSetOperation {

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