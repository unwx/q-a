package qa.cache.operation.impl;

import qa.cache.entity.like.set.AnswerToLikeSet;
import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.KeyOperation;
import qa.cache.operation.LikeSetOperationImpl;
import qa.util.RedisOperationUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

public class AnswerToLikeSetOperation extends LikeSetOperationImpl implements EntityToLikeSetOperation<Long> {

    @Override
    public boolean create(Long answerId, Jedis jedis) {
        final AnswerToLikeSet set = new AnswerToLikeSet(answerId);
        return super.create(set, jedis);
    }

    @Override
    public int get(Long answerId, Jedis jedis) {
        final AnswerToLikeSet set = new AnswerToLikeSet(answerId);
        return super.getK(set, jedis);
    }

    @Override
    public List<Integer> get(List<Long> answers, Jedis jedis) {
        final List<KeyOperation> sets = RedisOperationUtil.toKeyOperation(answers, AnswerToLikeSet::new);
        return super.getK(sets, jedis);
    }

    @Override
    public long increment(Long answerId, Jedis jedis) {
        final AnswerToLikeSet set = new AnswerToLikeSet(answerId);
        return super.increment(set, jedis);
    }

    @Override
    public boolean delete(Long answerId, Jedis jedis) {
        final AnswerToLikeSet set = new AnswerToLikeSet(answerId);
        return super.delete(set, jedis);
    }
}