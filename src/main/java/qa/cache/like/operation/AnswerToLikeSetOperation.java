package qa.cache.like.operation;

import org.springframework.stereotype.Component;
import qa.cache.abstraction.KeyOperation;
import qa.cache.abstraction.LikeSetOperationImpl;
import qa.cache.like.entity.AnswerToLikeSet;
import qa.util.RedisOperationUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

@Component
public class AnswerToLikeSetOperation extends LikeSetOperationImpl implements EntityToLikeSetOperation {

    private static final String name = "answer";

    @Override
    public boolean create(String answerId, Jedis jedis) {
        final AnswerToLikeSet set = new AnswerToLikeSet(answerId);
        return super.create(set, jedis);
    }

    @Override
    public int get(String answerId, Jedis jedis) {
        final AnswerToLikeSet set = new AnswerToLikeSet(answerId);
        return super.getK(set, jedis);
    }

    @Override
    public List<Integer> get(List<String> answers, Jedis jedis) {
        final List<KeyOperation> sets = RedisOperationUtil.toKeyOperation(answers, AnswerToLikeSet::new);
        return super.getK(sets, jedis);
    }

    @Override
    public long increment(String answerId, Jedis jedis) {
        final AnswerToLikeSet set = new AnswerToLikeSet(answerId);
        return super.increment(set, jedis);
    }

    @Override
    public boolean delete(String answerId, Jedis jedis) {
        final AnswerToLikeSet set = new AnswerToLikeSet(answerId);
        return super.delete(set, jedis);
    }

    @Override
    public String name() {
        return name;
    }
}