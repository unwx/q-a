package qa.util;

import qa.cache.operation.KeyOperation;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RedisOperationUtil {

    private RedisOperationUtil() {}

    public static <T> List<KeyOperation> toKeyOperation(List<T> ids, Function<? super T, ? extends KeyOperation> function) {
        if (ids.isEmpty())
            return Collections.emptyList();
        return ids
                        .stream()
                        .map(function)
                        .collect(Collectors.toList());
    }
}
