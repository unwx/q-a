package qa.util;

import qa.cache.abstraction.KeyOperation;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RedisOperationUtil {

    private RedisOperationUtil() {}

    public static List<KeyOperation> toKeyOperation(List<String> ids, Function<String, ? extends KeyOperation> function) {
        if (ids.isEmpty())
            return Collections.emptyList();
        return ids
                        .stream()
                        .map(function)
                        .collect(Collectors.toList());
    }
}
