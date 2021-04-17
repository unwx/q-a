package qa.cache.operation;

import org.jetbrains.annotations.Nullable;
import qa.cache.KeyOperation;

import java.util.List;

public interface LikeSetOperation<R extends KeyOperation> {
    Long create(R r);

    @Nullable Integer getK(R r);

    List<Integer> getK(List<R> r);

    Long increment(R r);

    Long delete(R r);
}
