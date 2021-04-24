package qa.cache.operation.impl;

import qa.cache.entity.like.set.CommentQuestionToLikeSet;
import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.KeyOperation;
import qa.cache.operation.LikeSetOperationImpl;
import qa.util.RedisOperationUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

public class CommentQuestionToLikeSetOperation extends LikeSetOperationImpl implements EntityToLikeSetOperation<Long> {

    @Override
    public boolean create(Long commentId, Jedis jedis) {
        final CommentQuestionToLikeSet set = new CommentQuestionToLikeSet(commentId);
        return super.create(set, jedis);
    }

    @Override
    public int get(Long commentId, Jedis jedis) {
        final CommentQuestionToLikeSet set = new CommentQuestionToLikeSet(commentId);
        return super.getK(set, jedis);
    }

    @Override
    public List<Integer> get(List<Long> commentIds, Jedis jedis) {
        final List<KeyOperation> sets = RedisOperationUtil.toKeyOperation(commentIds, CommentQuestionToLikeSet::new);
        return super.getK(sets, jedis);
    }

    @Override
    public long increment(Long commentId, Jedis jedis) {
        final CommentQuestionToLikeSet set = new CommentQuestionToLikeSet(commentId);
        return super.increment(set, jedis);
    }

    @Override
    public boolean delete(Long commentId, Jedis jedis) {
        final CommentQuestionToLikeSet set = new CommentQuestionToLikeSet(commentId);
        return super.delete(set, jedis);
    }
}
