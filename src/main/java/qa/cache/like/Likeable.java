package qa.cache.like;

public interface Likeable<T> {
    void like(long userId, T id);
}