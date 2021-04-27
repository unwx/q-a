package qa.cache.entity.like;

import qa.cache.Cache;

public interface HasLiked extends Cache {
    void setLiked(boolean liked);

    boolean isLiked();
}
