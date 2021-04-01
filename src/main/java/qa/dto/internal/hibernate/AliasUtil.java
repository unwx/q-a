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
}
