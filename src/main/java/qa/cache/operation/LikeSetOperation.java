package qa.cache.operation;

import java.util.List;

public interface LikeSetOperation {
    boolean create(KeyOperation r);

    int getK(KeyOperation r);

    <T> List<Integer> getK(List<KeyOperation> r);

    long increment(KeyOperation r);

    boolean delete(KeyOperation r);
}