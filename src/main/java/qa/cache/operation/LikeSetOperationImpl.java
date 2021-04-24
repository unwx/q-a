package qa.cache.operation;

import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class LikeSetOperationImpl extends SetSizeOperation implements LikeSetOperation {

    private final Jedis jedis;

    public LikeSetOperationImpl(Jedis jedis) {
        super(jedis);
        this.jedis = jedis;
    }

    @Override
    public boolean create(KeyOperation r) {
        return jedis.setnx(r.getKey(), "0") == 1;
    }

    @Override
    public int getK(KeyOperation r) {
        String result = super.getS(r.getKey());
        return result == null ? -1 : Integer.parseInt(result);
    }

    @Override
    public <T> List<Integer> getK(List<KeyOperation> r) {
        if (r.isEmpty())
            return Collections.emptyList();

        List<String> result = super.getS(
                r
                        .stream()
                        .map(KeyOperation::getKey)
                        .collect(Collectors.toList()));
        return result
                .stream()
                .map((v) -> v == null ? -1 : Integer.parseInt(v))
                .collect(Collectors.toList());
    }

    @Override
    public long increment(KeyOperation r) {
        return jedis.incr(r.getKey());
    }

    @Override
    public boolean delete(KeyOperation r) {
        return jedis.del(r.getKey()) == 1;
    }
}
