package qa.cache.entity.like;

public interface HasLikes {
    String getIdStr();

    void setLikes(int count);

    int getLikes();
}