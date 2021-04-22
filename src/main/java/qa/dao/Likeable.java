package qa.dao;

public interface Likeable<T> {
    void like(long userId, T id);
}