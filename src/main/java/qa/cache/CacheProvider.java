package qa.cache;

import org.jetbrains.annotations.NotNull;
import qa.cache.entity.like.HasLiked;
import qa.cache.entity.like.HasLikes;
import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.IUserEntityLikeSetOperation;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CacheProvider {

    protected <H extends HasLikes & HasLiked> H provide(@NotNull H entity,
                                                        String userId,
                                                        IUserEntityLikeSetOperation userEntityOperation,
                                                        EntityToLikeSetOperation entityOperation,
                                                        Jedis jedis) {

        return this.getSet(entity, userId, userEntityOperation, entityOperation, jedis);
    }

    protected <H extends HasLikes> H provide(@NotNull H entity,
                                             EntityToLikeSetOperation entityOperation,
                                             Jedis jedis) {

        return this.getSet(entity, entityOperation, jedis);
    }

    protected <H extends HasLikes & HasLiked> List<H> provide(List<H> entities,
                                                              String userId,
                                                              IUserEntityLikeSetOperation userEntityOperation,
                                                              EntityToLikeSetOperation entityOperation,
                                                              Jedis jedis) {

        return this.getSet(entities, userId, userEntityOperation, entityOperation, jedis);
    }

    protected <H extends HasLikes> List<H> provide(List<H> entities,
                                                   EntityToLikeSetOperation entityOperation,
                                                   Jedis jedis) {

        return this.getSet(entities, entityOperation, jedis);
    }

    private <H extends HasLikes> List<H> getSet(List<H> entities,
                                                EntityToLikeSetOperation entityOperation,
                                                Jedis jedis) {

        final List<String> entityIds = entities
                .stream()
                .map(HasLikes::getIdStr)
                .collect(Collectors.toList());
        final List<Integer> likes = entityOperation.get(entityIds, jedis);

        for (int i = 0; i < entities.size(); i++) {
            final H entity = entities.get(i);
            final int like = likes.get(i);

            entity.setLikes(like);
        }
        return entities;
    }

    private <H extends HasLikes & HasLiked> List<H> getSet(List<H> entities,
                                                           String userId,
                                                           IUserEntityLikeSetOperation userEntityOperation,
                                                           EntityToLikeSetOperation entityOperation,
                                                           Jedis jedis) {

        final int size = entities.size();
        final List<String> entityIds = new ArrayList<>(size);

        for (final H entity : entities) {
            final String entityId = entity.getIdStr();
            entityIds.add(entityId);
            entity.setLiked(userEntityOperation.isValueExist(userId, entityId, jedis));
        }

        final List<Integer> likes = entityOperation.get(entityIds, jedis);
        for (int i = 0; i < size; i++) {
            final H entity = entities.get(i);
            final int like = likes.get(i);

            entity.setLikes(like);
        }
        return entities;
    }

    private <H extends HasLikes> H getSet(@NotNull H entity,
                                          EntityToLikeSetOperation entityOperation,
                                          Jedis jedis) {

        final String entityId = entity.getIdStr();
        final int likes = entityOperation.get(entityId, jedis);

        entity.setLikes(likes);
        return entity;
    }

    private <H extends HasLikes & HasLiked> H getSet(@NotNull H entity,
                                                     String userId,
                                                     IUserEntityLikeSetOperation userEntityOperation,
                                                     EntityToLikeSetOperation entityOperation,
                                                     Jedis jedis) {

        final String entityId = entity.getIdStr();
        final int likes = entityOperation.get(entityId, jedis);
        final boolean liked = userEntityOperation.isValueExist(userId, entityId, jedis);

        entity.setLikes(likes);
        entity.setLiked(liked);

        return entity;
    }
}
