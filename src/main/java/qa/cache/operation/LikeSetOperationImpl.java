package qa.cache.operation;

import org.jetbrains.annotations.Nullable;
import qa.cache.KeyOperation;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.stream.Collectors;

public abstract class LikeSetOperationImpl<R extends KeyOperation> extends SetSizeOperation implements LikeSetOperation<R> {

    private final Jedis jedis;

    public LikeSetOperationImpl(Jedis jedis) {
        super(jedis);
        this.jedis = jedis;
    }

    @Override
    public Long create(R r) {
        Long result = jedis.setnx(r.getKey(), "0");
        jedis.close();
        return result;
    }

    @Override
    @Nullable
    public Integer getK(R r) {
        String result = super.getS(r.getKey());
        jedis.close();
        return result == null ? null : Integer.parseInt(result);
    }

    @Override
    public List<Integer> getK(List<R> r) {
        List<String> result = super.getS(
                r
                        .stream()
                        .map(R::getKey)
                        .collect(Collectors.toList()));
        return result
                .stream()
                .map((v) -> v == null ? null : Integer.parseInt(v))
                .collect(Collectors.toList());
    }

    @Override
    public Long increment(R r) {
        return jedis.incr(r.getKey());
    }

    @Override
    public Long delete(R r) {
        return jedis.del(r.getKey());
    }
}
