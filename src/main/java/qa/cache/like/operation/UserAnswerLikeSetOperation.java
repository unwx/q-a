package qa.cache.like.operation;

import org.springframework.stereotype.Component;
import qa.cache.RedisKeys;
import qa.cache.abstraction.UserEntitySetOperation;
import qa.cache.like.entity.AnswerToUserLikeSet;
import qa.cache.like.entity.UserToAnswerLikeSet;
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