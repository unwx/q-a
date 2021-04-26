package qa.cache.entity.like;

import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.IUserEntityLikeSetOperation;
import qa.exceptions.dao.EntityAlreadyCreatedException;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.stream.Collectors;

public class LikesUtil { // LIKES PROVIDER

    private static final String ERR_ALREADY_EXIST = "entity already exist. id: %s";

    private LikesUtil() {}

    public static boolean like(String userId,
                            String entityId,
                            IUserEntityLikeSetOperation userEntityOperation,
                            EntityToLikeSetOperation entityOperation,
                            Jedis jedis) {

        final boolean status = userEntityOperation.add(userId, entityId, jedis);
        if (status) entityOperation.increment(entityId, jedis);
        return status;
    }

    @Deprecated
    public static boolean deleteLikes(String entityId,
                                      IUserEntityLikeSetOperation userEntityOperation,
                                      EntityToLikeSetOperation entityOperation,
                                      Jedis jedis) {

        final boolean status = userEntityOperation.deleteEntity(entityId, jedis);
        if (status) entityOperation.delete(entityId, jedis);
        return status;
    }

    public static void createLike(String id,
                                  EntityToLikeSetOperation operation,
                                  Jedis jedis) {
        final boolean reply = operation.create(id, jedis);
        if (!reply)
            throw new EntityAlreadyCreatedException(ERR_ALREADY_EXIST.formatted(id));
    }

    public static <T extends HasLikes> void setLikes(List<T> hasLikes,
                                                     EntityToLikeSetOperation operation,
                                                     Jedis jedis) {
        final List<Integer> likes = operation.get(
                hasLikes
                        .stream()
                        .map(HasLikes::getIdStr)
                        .collect(Collectors.toList()),
                jedis
        );
        setLikesProcess(hasLikes, likes, operation, jedis);
    }

    public static <T extends HasLikes & HasLiked> void setLikesAndLiked(List<T> hasLikes,
                                                                        String userId,
                                                                        EntityToLikeSetOperation operation,
                                                                        IUserEntityLikeSetOperation userToEntitySetOperation,
                                                                        Jedis jedis) {
        setLikes(hasLikes, operation, jedis);
        hasLikes.forEach((e) -> setLiked(e, e.getIdStr(), userId, userToEntitySetOperation, jedis));
    }

    public static <T extends HasLikes & HasLiked> void setLikeAndLiked(T entity,
                                                                       String userId,
                                                                       EntityToLikeSetOperation entityToLikeSetOperation,
                                                                       IUserEntityLikeSetOperation userToEntitySetOperation,
                                                                       Jedis jedis) {
        final String id = entity.getIdStr();
        final int likes = entityToLikeSetOperation.get(id, jedis);

        setLikeProcess(entity, likes, entityToLikeSetOperation, jedis);
        setLiked(entity, id, userId, userToEntitySetOperation, jedis);
    }

    public static <T extends HasLikes> void setLikesProcess(List<T> hasLikes,
                                                            List<Integer> likes,
                                                            EntityToLikeSetOperation onFailure,
                                                            Jedis jedis) {
        for (int i = 0; i < likes.size(); i++) {
            T hasLike = hasLikes.get(i);
            Integer like = likes.get(i);

            setLikeProcess(hasLike, like, onFailure, jedis);
        }
    }

    private static void setLikeProcess(HasLikes hasLikes,
                                       Integer like,
                                       EntityToLikeSetOperation onFailure,
                                       Jedis jedis) {
        if (like == -1) {
            hasLikes.setLikes(0);
            onFailure.create(hasLikes.getIdStr(), jedis);
        } else
            hasLikes.setLikes(like);
    }

    private static void setLiked(HasLiked entity,
                                 String id,
                                 String userId,
                                 IUserEntityLikeSetOperation userToEntitySetOperation,
                                 Jedis jedis) {
        final boolean liked = userToEntitySetOperation.isValueExist(userId, id, jedis);
        entity.setLiked(liked);
    }
}
