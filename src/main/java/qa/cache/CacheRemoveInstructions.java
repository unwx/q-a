package qa.cache;

import qa.domain.DomainName;

import java.util.EnumMap;
import java.util.Map;
import java.util.Stack;

public class CacheRemoveInstructions {

    private final Map<DomainName, Stack<String>> nameToIdsMap = new EnumMap<>(DomainName.class);

    public void addInstruction(DomainName name, Stack<String> ids) {
        this.nameToIdsMap.put(name, ids);
    }

    public Map<DomainName, Stack<String>> getInstructions() {
        return nameToIdsMap;
    }
}
