package qa.cache.entity.like.provider.remover;

import qa.cache.CacheLikeOperation;

import java.util.Stack;

public class CacheRemoveInstruction {

    private final CacheLikeOperation operation;
    private final Stack<String> ids;

    public CacheRemoveInstruction(CacheLikeOperation operation,
                                  Stack<String> ids) {
        this.operation = operation;
        this.ids = ids;
    }

    public CacheLikeOperation getOperation() {
        return operation;
    }

    public Stack<String> getIds() {
        return ids;
    }
}
