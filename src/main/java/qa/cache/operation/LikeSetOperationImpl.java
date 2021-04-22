package qa.cache.operation;

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
    public boolean create(R r) {
        return jedis.setnx(r.getKey(), "0") == 1;
    }

    @Override
    public int getK(R r) {
        String result = super.getS(r.getKey());
        return result == null ? -1 : Integer.parseInt(result);
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
                .map((v) -> v == null ? -1 : Integer.parseInt(v))
                .collect(Collectors.toList());
    }

    @Override
    public long increment(R r) {
        return jedis.incr(r.getKey());
    }

    @Override
    public boolean delete(R r) {
        return jedis.del(r.getKey()) == 1;
    }
}
