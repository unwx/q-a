package qa.cache.entity.like;

import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.IUserToEntitySetOperation;
import qa.exceptions.dao.EntityAlreadyCreatedException;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.stream.Collectors;

public class LikesUtil {

    private static final String ERR_ALREADY_EXIST = "entity already exist. id: %s";

    private LikesUtil() {}

    public static <R> void createLike(R id,
                                      EntityToLikeSetOperation<R> operation,
                                      Jedis jedis) {
        final boolean reply = operation.create(id, jedis);
        if (!reply)
            throw new EntityAlreadyCreatedException(ERR_ALREADY_EXIST.formatted(id));
    }

    public static <R, T extends HasLikes<R>> void setLikes(List<T> hasLikes,
                                                           EntityToLikeSetOperation<R> operation,
                                                           Jedis jedis) {
        final List<Integer> likes = operation.get(
                hasLikes
                        .stream()
                        .map(HasLikes::getId)
                        .collect(Collectors.toList()),
                jedis
        );
        setLikesProcess(hasLikes, likes, operation, jedis);
    }

    public static <R, T extends HasLikes<R> & HasLiked> void setLikesAndLiked(List<T> hasLikes,
                                                                              long userId,
                                                                              EntityToLikeSetOperation<R> operation,
                                                                              IUserToEntitySetOperation<R> userToEntitySetOperation,
                                                                              Jedis jedis) {
        setLikes(hasLikes, operation, jedis);
        hasLikes.forEach((e) -> setLiked(e, e.getId(), userId, userToEntitySetOperation, jedis));
    }

    public static <R, T extends HasLikes<R> & HasLiked> void setLikeAndLiked(T entity,
                                                                             long userId,
                                                                             EntityToLikeSetOperation<R> entityToLikeSetOperation,
                                                                             IUserToEntitySetOperation<R> userToEntitySetOperation,
                                                                             Jedis jedis) {
        final R id = entity.getId();
        final int likes = entityToLikeSetOperation.get(id, jedis);

        setLikeProcess(entity, likes, entityToLikeSetOperation, jedis);
        setLiked(entity, id, userId, userToEntitySetOperation, jedis);
    }

    public static <R, T extends HasLikes<R>> void setLikesProcess(List<T> hasLikes,
                                                                  List<Integer> likes,
                                                                  EntityToLikeSetOperation<R> onFailure,
                                                                  Jedis jedis) {
        for (int i = 0; i < likes.size(); i++) {
            T hasLike = hasLikes.get(i);
            Integer like = likes.get(i);

            setLikeProcess(hasLike, like, onFailure, jedis);
        }
    }

    private static <R> void setLikeProcess(HasLikes<R> hasLikes,
                                           Integer like,
                                           EntityToLikeSetOperation<R> onFailure,
                                           Jedis jedis) {
        if (like == -1) {
            hasLikes.setLikes(0);
            onFailure.create(hasLikes.getId(), jedis);
        } else
            hasLikes.setLikes(like);
    }

    private static <R> void setLiked(HasLiked entity,
                                     R id,
                                     long userId,
                                     IUserToEntitySetOperation<R> userToEntitySetOperation,
                                     Jedis jedis) {
        final boolean liked = userToEntitySetOperation.isValueExist(userId, id, jedis);
        entity.setLiked(liked);
    }
}
