package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.cache.like.CommentAnswerLikeProvider;
import qa.cache.like.Likeable;
import qa.dao.database.components.Where;
import qa.dao.database.components.WhereOperator;
import qa.dao.query.manager.CommentAnswerQueryManager;
import qa.domain.CommentAnswer;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.comment.answer.CommentAnswerDto;
import qa.exceptions.dao.NullResultException;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;

@Component
public class CommentAnswerDao extends DaoImpl<CommentAnswer> implements Likeable<Long> {

    private final SessionFactory sessionFactory;
    private final JedisResourceCenter jedisResourceCenter;
    private final CommentAnswerLikeProvider likesProvider;

    @Autowired
    public CommentAnswerDao(PropertySetterFactory propertySetterFactory,
                            SessionFactory sessionFactory,
                            JedisResourceCenter jedisResourceCenter,
                            CommentAnswerLikeProvider likesProvider) {

        super(sessionFactory, propertySetterFactory.getSetter(new CommentAnswer()));
        this.sessionFactory = sessionFactory;
        this.jedisResourceCenter = jedisResourceCenter;
        this.likesProvider = likesProvider;
    }

    @Override
    public Long create(CommentAnswer e) {
        final Long id = (Long) super.create(e);
        this.createLike(id);
        return id;
    }

    public void delete(long commentId) {
        final Where where = new Where("id", commentId, WhereOperator.EQUALS);
        super.delete(where);
        this.deleteLikes(commentId);
    }

    public boolean isExist(Long id) {
        return super.isExist(new Where("id", id, WhereOperator.EQUALS));
    }

    @Nullable
    public List<CommentAnswer> getComments(long answerId, long userId, int page) {

        /*
         *  if answer not exist: answers.size() = 0; (NullResultException will not be thrown) - return null
         *  if comments not exist: NullResultException - return empty list.
         *  if exist: return result.
         */

        final List<CommentAnswerDto> dto;
        final List<CommentAnswer> comments;

        try(Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            try {
                dto = CommentAnswerQueryManager
                                .commentsQuery(session, answerId, page)
                                .list();
            }
            catch (NullResultException ex) {
                transaction.rollback();
                return Collections.emptyList();
            }

            if (dto.isEmpty()) {
                transaction.rollback();
                return null;
            }

            transaction.commit();
        }

        comments = CommentAnswerQueryManager.dtoToCommentAnswerList(dto);
        this.setLikes(comments, userId);
        return comments;
    }

    @Override
    public void like(long userId, Long commentId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            this.likesProvider.like(userId, commentId, jedis);
        }
    }

    private void deleteLikes(long commentId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            this.likesProvider.remove(commentId, jedis);
        }
    }

    private void createLike(long commentId) {
        try(JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            this.likesProvider.initLike(commentId, jedis);
        }
    }

    private void setLikes(List<CommentAnswer> comments, long userId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            this.likesProvider.provide(comments, userId, jedis);
        }
    }
}
