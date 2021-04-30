package qa.cache.like;

import qa.cache.Cache;

public interface HasLikes extends Cache {
    void setLikes(int count);

    int getLikes();
}