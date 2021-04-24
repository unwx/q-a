package qa.util.dao.query.builder.redis;

import qa.cache.RedisKeys;
import redis.clients.jedis.Jedis;

public class CommentAnswerLikeQueryBuilder {

    private final Jedis jedis;

    public CommentAnswerLikeQueryBuilder(Jedis jedis) {
        this.jedis = jedis;
    }

    public void create(Long commentId) {
        jedis.setnx(RedisKeys.getCommentAnswerLikes(String.valueOf(commentId)), "0");
    }

    public void like(Long commentId) {
        jedis.incr(RedisKeys.getCommentAnswerLikes(String.valueOf(commentId)));
    }
}
