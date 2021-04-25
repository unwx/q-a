package qa.cache.operation.impl;

import qa.cache.entity.like.set.QuestionToLikeSet;
import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.KeyOperation;
import qa.cache.operation.LikeSetOperationImpl;
import qa.util.RedisOperationUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

public class QuestionToLikeSetOperation extends LikeSetOperationImpl implements EntityToLikeSetOperation<Long> {

    @Override
    public boolean create(Long questionId, Jedis jedis) {
        final QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.create(questionToLikeSet, jedis);
    }

    @Override
    public int get(Long questionId, Jedis jedis) {
        final QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.getK(questionToLikeSet, jedis);
    }

    @Override
    public List<Integer> get(List<Long> questionIds, Jedis jedis) {
        final List<KeyOperation> sets = RedisOperationUtil.toKeyOperation(questionIds, QuestionToLikeSet::new);
        return super.getK(sets, jedis);
    }

    @Override
    public long increment(Long questionId, Jedis jedis) {
        final QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.increment(questionToLikeSet, jedis);
    }

    @Override
    public boolean delete(Long questionId, Jedis jedis) {
        final QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.delete(questionToLikeSet, jedis);
    }
}
