package qa.cache;

import org.jetbrains.annotations.NotNull;
import qa.cache.entity.like.HasLiked;
import qa.cache.entity.like.HasLikes;
import qa.cache.operation.EntityToLikeSetOperation;
import qa.cache.operation.IUserEntityLikeSetOperation;
import qa.cache.operation.impl.*;
import qa.domain.DomainName;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CacheProvider extends CacheResolver { // TODO OPTIMIZE

    protected CacheProvider(QuestionToLikeSetOperation questionLikeOperation,
                            AnswerToLikeSetOperation answerLikeOperation,
                            CommentQuestionToLikeSetOperation commentQuestionLikeOperation,
                            CommentAnswerToLikeSetOperation commentAnswerLikeOperation,
                            UserQuestionLikeSetOperation userQuestionLikeOperation,
                            UserAnswerLikeSetOperation userAnswerLikeOperation,
                            UserCommentQuestionLikeSetOperation userCommentQuestionLikeOperation,
                            UserCommentAnswerLikeSetOperation userCommentAnswerLikeOperation) {
        super(
                questionLikeOperation,
                answerLikeOperation,
                commentQuestionLikeOperation,
                commentAnswerLikeOperation,
                userQuestionLikeOperation,
                userAnswerLikeOperation,
                userCommentQuestionLikeOperation,
                userCommentAnswerLikeOperation
        );
    }

    protected <H extends HasLikes & HasLiked> H provide(@NotNull H entity,
                                                        String userId,
                                                        DomainName name,
                                                        Jedis jedis) {

        final CacheLikeOperation operations = super.resolve(name);
        final IUserEntityLikeSetOperation userEntityOperation = operations.getUserEntitySetOperation();
        final EntityToLikeSetOperation entityOperation = operations.getEntityToLikeSetOperation();

        return this.getSet(entity, userId, userEntityOperation, entityOperation, jedis);
    }

    protected <H extends HasLikes> H provide(@NotNull H entity,
                                             DomainName name,
                                             Jedis jedis) {

        final EntityToLikeSetOperation entityOperation = super.resolveEntityToLike(name);
        return this.getSet(entity, entityOperation, jedis);
    }

    protected <H extends HasLikes & HasLiked> List<H> provide(List<H> entities,
                                                              String userId,
                                                              DomainName name,
                                                              Jedis jedis) {

        final CacheLikeOperation operations = super.resolve(name);
        final IUserEntityLikeSetOperation userEntityOperation = operations.getUserEntitySetOperation();
        final EntityToLikeSetOperation entityOperation = operations.getEntityToLikeSetOperation();

        return this.getSet(entities, userId, userEntityOperation, entityOperation, jedis);
    }

    protected <H extends HasLikes> List<H> provide(List<H> entities,
                                                   DomainName name,
                                                   Jedis jedis) {

        final EntityToLikeSetOperation entityOperation = super.resolveEntityToLike(name);
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
