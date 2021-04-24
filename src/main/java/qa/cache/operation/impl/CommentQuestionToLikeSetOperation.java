package qa.cache.operation.impl;

import qa.cache.entity.like.set.CommentQuestionToLikeSet;
import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.KeyOperation;
import qa.cache.operation.LikeSetOperationImpl;
import qa.util.RedisOperationUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

public class CommentQuestionToLikeSetOperation extends LikeSetOperationImpl implements EntityToLikeSetOperation<Long> {

    public CommentQuestionToLikeSetOperation(Jedis jedis) {
        super(jedis);
    }

    @Override
    public boolean create(Long commentId) {
        final CommentQuestionToLikeSet set = new CommentQuestionToLikeSet(commentId);
        return super.create(set);
    }

    @Override
    public int get(Long commentId) {
        final CommentQuestionToLikeSet set = new CommentQuestionToLikeSet(commentId);
        return super.getK(set);
    }

    @Override
    public List<Integer> get(List<Long> commentIds) {
        final List<KeyOperation> sets = RedisOperationUtil.toKeyOperation(commentIds, CommentQuestionToLikeSet::new);
        return super.getK(sets);
    }

    @Override
    public long increment(Long commentId) {
        final CommentQuestionToLikeSet set = new CommentQuestionToLikeSet(commentId);
        return super.increment(set);
    }

    @Override
    public boolean delete(Long commentId) {
        final CommentQuestionToLikeSet set = new CommentQuestionToLikeSet(commentId);
        return super.delete(set);
    }
}
