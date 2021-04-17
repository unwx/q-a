package qa.cache.entity.like;

public interface HasLikes<T> {
    T getId();

    void setLikes(Integer count);
}
