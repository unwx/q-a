package qa.cache;

import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.IUserEntityLikeSetOperation;

public class CacheLikeOperation {

    private final IUserEntityLikeSetOperation userEntitySetOperation;
    private final EntityToLikeSetOperation entityToLikeSetOperation;

    public CacheLikeOperation(IUserEntityLikeSetOperation userEntitySetOperation,
                              EntityToLikeSetOperation entityToLikeSetOperation) {
        this.userEntitySetOperation = userEntitySetOperation;
        this.entityToLikeSetOperation = entityToLikeSetOperation;
    }

    public EntityToLikeSetOperation getEntityToLikeSetOperation() {
        return entityToLikeSetOperation;
    }

    public IUserEntityLikeSetOperation getUserEntitySetOperation() {
        return userEntitySetOperation;
    }
}
