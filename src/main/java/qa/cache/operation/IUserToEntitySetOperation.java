package qa.cache.operation;

public interface IUserToEntitySetOperation<T> {

    boolean add(long userId, T entityId);

    boolean isValueExist(long userId, T entityId);

    boolean deleteValue(long userId, T entityId);

    boolean deleteKey(long userId);
}