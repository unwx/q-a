package qa.cache;

import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.IUserEntityLikeSetOperation;

public class CacheLikeOperation {

    private final EntityToLikeSetOperation entityToLikeSetOperation;
    private final IUserEntityLikeSetOperation userEntitySetOperation;

    public CacheLikeOperation(EntityToLikeSetOperation entityToLikeSetOperation,
                              IUserEntityLikeSetOperation userEntitySetOperation) {
        this.entityToLikeSetOperation = entityToLikeSetOperation;
        this.userEntitySetOperation = userEntitySetOperation;
    }

    public EntityToLikeSetOperation getEntityToLikeSetOperation() {
        return entityToLikeSetOperation;
    }

    public IUserEntityLikeSetOperation getUserEntitySetOperation() {
        return userEntitySetOperation;
    }
}
