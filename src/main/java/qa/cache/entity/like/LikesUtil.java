package qa.cache.entity.like;

import qa.cache.operation.HighLikeOperation;

import java.util.List;

public class LikesUtil {

    private LikesUtil() {
    }

    public static <R, T extends HasLikes<R>> void setLikesProcess(List<T> hasLikes,
                                                                  List<Integer> likes,
                                                                  HighLikeOperation<R> onFailure) {
        for (int i = 0; i < likes.size(); i++) {
            T hasLike = hasLikes.get(i);
            Integer like = likes.get(i);

            setLikeProcess(hasLike, like, onFailure);
        }
    }

    public static <R, T extends HasLikes<R>> void setLikeProcess(T hasLikes,
                                                                 Integer like,
                                                                 HighLikeOperation<R> onFailure) {
        if (like == null) {
            hasLikes.setLikes(0);
            onFailure.create(hasLikes.getId());
        } else
            hasLikes.setLikes(like);
    }
}
