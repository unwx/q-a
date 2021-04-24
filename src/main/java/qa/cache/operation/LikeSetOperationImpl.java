package qa.cache.operation;

import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class LikeSetOperationImpl extends SetSizeOperation implements LikeSetOperation {

    @Override
    public boolean create(KeyOperation r, Jedis jedis) {
        return jedis.setnx(r.getKey(), "0") == 1;
    }

    @Override
    public int getK(KeyOperation r, Jedis jedis) {
        final String result = super.getS(r.getKey(), jedis);
        return result == null ? -1 : Integer.parseInt(result);
    }

    @Override
    public <T> List<Integer> getK(List<KeyOperation> r, Jedis jedis) {
        if (r.isEmpty())
            return Collections.emptyList();

        final List<String> result = super.getS(
                r
                        .stream()
                        .map(KeyOperation::getKey)
                        .collect(Collectors.toList()),
                jedis
        );
        return result
                .stream()
                .map((v) -> v == null ? -1 : Integer.parseInt(v))
                .collect(Collectors.toList());
    }

    @Override
    public long increment(KeyOperation r, Jedis jedis) {
        return jedis.incr(r.getKey());
    }

    @Override
    public boolean delete(KeyOperation r, Jedis jedis) {
        return jedis.del(r.getKey()) == 1;
    }
}
