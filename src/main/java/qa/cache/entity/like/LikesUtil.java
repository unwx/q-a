package qa.cache.entity.like;

import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.exceptions.dao.EntityAlreadyCreatedException;

import java.util.List;
import java.util.stream.Collectors;

public class LikesUtil {

    private LikesUtil() {
    }

    public static <R> void createLike(R id, EntityToLikeSetOperation<R> operation) {
        final boolean reply = operation.create(id);
        if (!reply)
            throw new EntityAlreadyCreatedException("entity already exist. id: %s".formatted(id));
    }

    public static <R, T extends HasLikes<R>> void setLikes(List<T> hasLikes,
                                                           EntityToLikeSetOperation<R> operation) {
        final List<Integer> likes = operation.get(
                hasLikes
                        .stream()
                        .map(HasLikes::getId)
                        .collect(Collectors.toList())
        );
        setLikesProcess(hasLikes, likes, operation);
    }

    public static <R, T extends HasLikes<R> & HasLiked> void setLikesAndLiked(List<T> hasLikes,
                                                                              long userId,
                                                                              EntityToLikeSetOperation<R> operation,
                                                                              IUserToEntitySetOperation<R> userToEntitySetOperation) {
        setLikes(hasLikes, operation);
        hasLikes.forEach((e) -> setLiked(e, e.getId(), userId, userToEntitySetOperation));
    }

    public static <R, T extends HasLikes<R> & HasLiked> void setLikeAndLiked(T entity,
                                                                             long userId,
                                                                             EntityToLikeSetOperation<R> entityToLikeSetOperation,
                                                                             IUserToEntitySetOperation<R> userToEntitySetOperation) {
        final R id = entity.getId();
        final int likes = entityToLikeSetOperation.get(id);

        setLikeProcess(entity, likes, entityToLikeSetOperation);
        setLiked(entity, id, userId, userToEntitySetOperation);
    }

    public static <R, T extends HasLikes<R>> void setLikesProcess(List<T> hasLikes,
                                                                  List<Integer> likes,
                                                                  EntityToLikeSetOperation<R> onFailure) {
        for (int i = 0; i < likes.size(); i++) {
            T hasLike = hasLikes.get(i);
            Integer like = likes.get(i);

            setLikeProcess(hasLike, like, onFailure);
        }
    }

    private static <R> void setLikeProcess(HasLikes<R> hasLikes,
                                           Integer like,
                                           EntityToLikeSetOperation<R> onFailure) {
        if (like == -1) {
            hasLikes.setLikes(0);
            onFailure.create(hasLikes.getId());
        } else
            hasLikes.setLikes(like);
    }

    private static <R> void setLiked(HasLiked entity,
                                     R id,
                                     long userId,
                                     IUserToEntitySetOperation<R> userToEntitySetOperation) {
        final boolean liked = userToEntitySetOperation.isValueExist(userId, id);
        entity.setLiked(liked);
    }
}
