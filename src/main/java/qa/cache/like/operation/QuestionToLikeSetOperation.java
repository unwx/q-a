package qa.cache.like.operation;

import org.springframework.stereotype.Component;
import qa.cache.abstraction.KeyOperation;
import qa.cache.abstraction.LikeSetOperationImpl;
import qa.cache.like.entity.QuestionToLikeSet;
import qa.util.RedisOperationUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

@Component
public class QuestionToLikeSetOperation extends LikeSetOperationImpl implements EntityToLikeSetOperation {

    private static final String name = "question";

    @Override
    public boolean create(String questionId, Jedis jedis) {
        final QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.create(questionToLikeSet, jedis);
    }

    @Override
    public int get(String questionId, Jedis jedis) {
        final QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.getK(questionToLikeSet, jedis);
    }

    @Override
    public List<Integer> get(List<String> questionIds, Jedis jedis) {
        final List<KeyOperation> sets = RedisOperationUtil.toKeyOperation(questionIds, QuestionToLikeSet::new);
        return super.getK(sets, jedis);
    }

    @Override
    public long increment(String questionId, Jedis jedis) {
        final QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.increment(questionToLikeSet, jedis);
    }

    @Override
    public boolean delete(String questionId, Jedis jedis) {
        final QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.delete(questionToLikeSet, jedis);
    }

    @Override
    public String name() {
        return name;
    }
}
