package qa.cache.entity.like;

import qa.cache.Cache;

public interface HasLikes extends Cache {
    void setLikes(int count);

    int getLikes();
}