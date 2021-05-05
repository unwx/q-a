package qa.cache.like.operation.impl;

import org.springframework.stereotype.Component;
import qa.cache.abstraction.KeyOperation;
import qa.cache.abstraction.LikeSetOperationImpl;
import qa.cache.like.entity.CommentQuestionToLikeSet;
import qa.cache.like.operation.CommentToLikeSetOperation;
import qa.cache.like.operation.util.RedisOperationUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

@Component
public class CommentQuestionToLikeSetOperation extends LikeSetOperationImpl implements CommentToLikeSetOperation {

    private static final String name = "comment-question";

    @Override
    public boolean create(String commentId, Jedis jedis) {
        final CommentQuestionToLikeSet set = new CommentQuestionToLikeSet(commentId);
        return super.create(set, jedis);
    }

    @Override
    public int get(String commentId, Jedis jedis) {
        final CommentQuestionToLikeSet set = new CommentQuestionToLikeSet(commentId);
        return super.getK(set, jedis);
    }

    @Override
    public List<Integer> get(List<String> commentIds, Jedis jedis) {
        final List<KeyOperation> sets = RedisOperationUtil.toKeyOperation(commentIds, CommentQuestionToLikeSet::new);
        return super.getK(sets, jedis);
    }

    @Override
    public long increment(String commentId, Jedis jedis) {
        final CommentQuestionToLikeSet set = new CommentQuestionToLikeSet(commentId);
        return super.increment(set, jedis);
    }

    @Override
    public boolean delete(String commentId, Jedis jedis) {
        final CommentQuestionToLikeSet set = new CommentQuestionToLikeSet(commentId);
        return super.delete(set, jedis);
    }

    @Override
    public String name() {
        return name;
    }
}
