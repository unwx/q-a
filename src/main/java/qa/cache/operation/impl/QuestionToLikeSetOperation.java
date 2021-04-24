package qa.cache.operation.impl;

import qa.cache.entity.like.set.QuestionToLikeSet;
import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.KeyOperation;
import qa.cache.operation.LikeSetOperationImpl;
import qa.util.RedisOperationUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

public class QuestionToLikeSetOperation extends LikeSetOperationImpl implements EntityToLikeSetOperation<Long> {

    public QuestionToLikeSetOperation(Jedis jedis) {
        super(jedis);
    }

    /**
     * after creating a question - the initial number of likes is set - 0
     */
    @Override
    public boolean create(Long questionId) {
        QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.create(questionToLikeSet);
    }

    @Override
    public int get(Long questionId) {
        QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.getK(questionToLikeSet);
    }

    @Override
    public List<Integer> get(List<Long> questionIds) {
        final List<KeyOperation> sets = RedisOperationUtil.toKeyOperation(questionIds, QuestionToLikeSet::new);
        return super.getK(sets);
    }

    @Override
    public long increment(Long questionId) {
        QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.increment(questionToLikeSet);
    }

    @Override
    public boolean delete(Long questionId) {
        QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.delete(questionToLikeSet);
    }
}
