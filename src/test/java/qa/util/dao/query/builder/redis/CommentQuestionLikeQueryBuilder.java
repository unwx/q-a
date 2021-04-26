package qa.util.dao.query.builder.redis;

import qa.cache.RedisKeys;
import redis.clients.jedis.Jedis;

public class CommentQuestionLikeQueryBuilder {

    private final Jedis jedis;

    public CommentQuestionLikeQueryBuilder(Jedis jedis) {
        this.jedis = jedis;
    }

    public void create(String commentId) {
        jedis.append(RedisKeys.getCommentQuestionLikes(commentId), "0");
    }

    public void like(Long commentId) {
        jedis.incr(RedisKeys.getCommentQuestionLikes(String.valueOf(commentId)));
    }
}
