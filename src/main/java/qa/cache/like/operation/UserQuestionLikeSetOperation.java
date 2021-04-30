package qa.cache.like.operation;

import org.springframework.stereotype.Component;
import qa.cache.RedisKeys;
import qa.cache.abstraction.UserEntitySetOperation;
import qa.cache.like.entity.QuestionToUserLikeSet;
import qa.cache.like.entity.UserToQuestionLikeSet;
import redis.clients.jedis.Jedis;

@Component
public class UserQuestionLikeSetOperation extends UserEntitySetOperation implements IUserEntityLikeSetOperation {

    @Override
    public boolean add(String userId, String questionId, Jedis jedis) {
        final UserToQuestionLikeSet userQuestionSet = new UserToQuestionLikeSet(userId, questionId);
        final QuestionToUserLikeSet questionUserSet = new QuestionToUserLikeSet(questionId, userId);

        final boolean reply = super.add(userQuestionSet, jedis);
        if (reply) super.add(questionUserSet, jedis);
        return reply;
    }

    @Override
    public boolean isValueExist(String userId, String questionId, Jedis jedis) {
        final UserToQuestionLikeSet set = new UserToQuestionLikeSet(userId, questionId);
        return super.isValueExist(set, jedis);
    }

    @Override
    public boolean deleteEntity(String questionId, Jedis jedis) {
        final QuestionToUserLikeSet questionUserSet = new QuestionToUserLikeSet(questionId, "-1");
        return super.deleteLinks(
                questionUserSet.getKey(),
                questionUserSet.getQuestionId(),
                RedisKeys.USER_QUESTION_LIKES,
                jedis
        );
    }
}
