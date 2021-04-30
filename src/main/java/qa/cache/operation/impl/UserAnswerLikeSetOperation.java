package qa.cache.operation.impl;

import org.springframework.stereotype.Component;
import qa.cache.RedisKeys;
import qa.cache.entity.like.set.AnswerToUserLikeSet;
import qa.cache.entity.like.set.UserToAnswerLikeSet;
import qa.cache.operation.IUserEntityLikeSetOperation;
import qa.cache.operation.UserEntitySetOperation;
import redis.clients.jedis.Jedis;

@Component
public class UserAnswerLikeSetOperation extends UserEntitySetOperation implements IUserEntityLikeSetOperation {

    @Override
    public boolean add(String userId, String answerId, Jedis jedis) {
        final UserToAnswerLikeSet userAnswerSet = new UserToAnswerLikeSet(userId, answerId);
        final AnswerToUserLikeSet answerUserSet = new AnswerToUserLikeSet(answerId, userId);

        final boolean reply = super.add(userAnswerSet, jedis);
        if (reply) super.add(answerUserSet, jedis);
        return reply;
    }

    @Override
    public boolean isValueExist(String userId, String answerId, Jedis jedis) {
        final UserToAnswerLikeSet set = new UserToAnswerLikeSet(userId, answerId);
        return super.isValueExist(set, jedis);
    }

    @Override
    public boolean deleteEntity(String answerId, Jedis jedis) {
        final AnswerToUserLikeSet answerUserSet = new AnswerToUserLikeSet(answerId, "-1");
        return super.deleteLinks(
                answerUserSet.getKey(),
                answerUserSet.getAnswerId(),
                RedisKeys.USER_ANSWER_LIKES,
                jedis
        );
    }
}