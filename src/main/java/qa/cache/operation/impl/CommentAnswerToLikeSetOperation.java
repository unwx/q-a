package qa.cache.operation.impl;

import org.springframework.stereotype.Component;
import qa.cache.entity.like.set.CommentAnswerToLikeSet;
import qa.cache.operation.CommentToLikeSetOperation;
import qa.cache.operation.KeyOperation;
import qa.cache.operation.LikeSetOperationImpl;
import qa.util.RedisOperationUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

@Component
public class CommentAnswerToLikeSetOperation extends LikeSetOperationImpl implements CommentToLikeSetOperation {

    @Override
    public boolean create(String commentId, Jedis jedis) {
        final CommentAnswerToLikeSet set = new CommentAnswerToLikeSet(commentId);
        return super.create(set, jedis);
    }

    @Override
    public int get(String commentId, Jedis jedis) {
        final CommentAnswerToLikeSet set = new CommentAnswerToLikeSet(commentId);
        return super.getK(set, jedis);
    }

    @Override
    public List<Integer> get(List<String> commentIds, Jedis jedis) {
        final List<KeyOperation> sets = RedisOperationUtil.toKeyOperation(commentIds, CommentAnswerToLikeSet::new);
        return super.getK(sets, jedis);
    }

    @Override
    public long increment(String commentId, Jedis jedis) {
        final CommentAnswerToLikeSet set = new CommentAnswerToLikeSet(commentId);
        return super.increment(set, jedis);
    }

    @Override
    public boolean delete(String commentId, Jedis jedis) {
        final CommentAnswerToLikeSet set = new CommentAnswerToLikeSet(commentId);
        return super.delete(set, jedis);
    }
}
