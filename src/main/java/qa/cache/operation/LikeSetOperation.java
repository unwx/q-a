package qa.cache.operation;

import qa.cache.KeyOperation;

import java.util.List;

public interface LikeSetOperation<R extends KeyOperation> {
    boolean create(R r);

    int getK(R r);

    List<Integer> getK(List<R> r);

    long increment(R r);

    boolean delete(R r);
}
