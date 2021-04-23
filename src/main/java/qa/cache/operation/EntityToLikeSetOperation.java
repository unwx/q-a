package qa.cache.operation;

import java.util.List;

public interface EntityToLikeSetOperation<T> {
    boolean create(T t);

    int get(T t);

    List<Integer> get(List<T> t);

    long increment(T t);

    boolean delete(T t);
}
