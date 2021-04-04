package qa.dto.internal.hibernate;

import java.util.LinkedHashMap;
import java.util.Map;

public class AliasUtil {

    private AliasUtil() {

    }

    public static Map<String, Integer> aliasToIndexMap(String[] aliases) {
        Map<String, Integer> aliasToIndexMap = new LinkedHashMap<>();
        for (int i = 0; i < aliases.length; i++) {
            aliasToIndexMap.put(aliases[i], i);
        }
        return aliasToIndexMap;
    }

    @SuppressWarnings("unchecked")
    public static <T> T setIfNotNull(String as, Map<String, Integer> aliasToIndexMap, Object[] tuples) {
        if (aliasToIndexMap.containsKey(as))
            return (T) tuples[aliasToIndexMap.get(as)];
        return null;
    }
}
