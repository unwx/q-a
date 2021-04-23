package qa.cache.operation.impl;

import qa.cache.entity.like.set.AnswerToLikeSet;
import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.KeyOperation;
import qa.cache.operation.LikeSetOperationImpl;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AnswerToLikeSetOperation extends LikeSetOperationImpl<AnswerToLikeSet> implements EntityToLikeSetOperation<Long> {

    public AnswerToLikeSetOperation(Jedis jedis) {
        super(jedis);
    }

    @Override
    public boolean create(Long answerId) {
        final AnswerToLikeSet set = new AnswerToLikeSet(answerId);
        return super.create(set);
    }

    @Override
    public int get(Long answerId) {
        final AnswerToLikeSet set = new AnswerToLikeSet(answerId);
        return super.getK(set);
    }

    @Override
    public List<Integer> get(List<Long> answers) {
        if (answers.isEmpty())
            return Collections.emptyList();

        final List<KeyOperation> questionToLikeSets =
                answers
                        .stream()
                        .map(AnswerToLikeSet::new)
                        .collect(Collectors.toList());

        return super.getK(questionToLikeSets);
    }

    @Override
    public long increment(Long answerId) {
        final AnswerToLikeSet set = new AnswerToLikeSet(answerId);
        return super.increment(set);
    }

    @Override
    public boolean delete(Long answerId) {
        final AnswerToLikeSet set = new AnswerToLikeSet(answerId);
        return super.delete(set);
    }
}