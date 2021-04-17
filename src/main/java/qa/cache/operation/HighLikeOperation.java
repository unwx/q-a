package qa.cache.operation;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface HighLikeOperation<T> {
    Long create(T t);

    @Nullable Integer get(T t);

    List<Integer> get(List<T> t);

    Long increment(T t);

    Long delete(T t);
}
