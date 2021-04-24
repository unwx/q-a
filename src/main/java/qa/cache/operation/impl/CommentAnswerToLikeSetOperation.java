package qa.cache.operation.impl;

import qa.cache.entity.like.set.CommentAnswerToLikeSet;
import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.KeyOperation;
import qa.cache.operation.LikeSetOperationImpl;
import qa.util.RedisOperationUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

public class CommentAnswerToLikeSetOperation extends LikeSetOperationImpl implements EntityToLikeSetOperation<Long> {

    @Override
    public boolean create(Long commentId, Jedis jedis) {
        final CommentAnswerToLikeSet set = new CommentAnswerToLikeSet(commentId);
        return super.create(set, jedis);
    }

    @Override
    public int get(Long commentId, Jedis jedis) {
        final CommentAnswerToLikeSet set = new CommentAnswerToLikeSet(commentId);
        return super.getK(set, jedis);
    }

    @Override
    public List<Integer> get(List<Long> commentIds, Jedis jedis) {
        final List<KeyOperation> sets = RedisOperationUtil.toKeyOperation(commentIds, CommentAnswerToLikeSet::new);
        return super.getK(sets, jedis);
    }

    @Override
    public long increment(Long commentId, Jedis jedis) {
        final CommentAnswerToLikeSet set = new CommentAnswerToLikeSet(commentId);
        return super.increment(set, jedis);
    }

    @Override
    public boolean delete(Long commentId, Jedis jedis) {
        final CommentAnswerToLikeSet set = new CommentAnswerToLikeSet(commentId);
        return super.delete(set, jedis);
    }
}
