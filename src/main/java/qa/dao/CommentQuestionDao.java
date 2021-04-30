package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.cache.like.CommentQuestionLikeProvider;
import qa.cache.like.Likeable;
import qa.dao.database.components.Where;
import qa.dao.database.components.WhereOperator;
import qa.dao.query.manager.CommentQuestionQueryManager;
import qa.domain.CommentQuestion;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.comment.question.CommentQuestionDto;
import qa.exceptions.dao.NullResultException;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;

@Component
public class CommentQuestionDao extends DaoImpl<CommentQuestion> implements Likeable<Long> {

    private final SessionFactory sessionFactory;
    private final JedisResourceCenter jedisResourceCenter;
    private final CommentQuestionLikeProvider likesProvider;

    @Autowired
    public CommentQuestionDao(PropertySetterFactory propertySetterFactory,
                              SessionFactory sessionFactory,
                              JedisResourceCenter jedisResourceCenter,
                              CommentQuestionLikeProvider likesProvider) {

        super(sessionFactory, propertySetterFactory.getSetter(new CommentQuestion()));
        this.sessionFactory = sessionFactory;
        this.jedisResourceCenter = jedisResourceCenter;
        this.likesProvider = likesProvider;
    }

    @Override
    public Long create(CommentQuestion e) {
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
    public List<CommentQuestion> getComments(long questionId, long userId, int page) {

        /*
         *  if question not exist: comments.size() = 0; (NullResultException will not be thrown) - return null
         *  if comments not exist: NullResultException - return empty list.
         *  if exist: return result.
         */

        final List<CommentQuestionDto> dto;
        final List<CommentQuestion> comments;

        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            try {
                dto = CommentQuestionQueryManager
                                .commentsQuery(session, questionId, page)
                                .list();
            } catch (NullResultException ex) { // comments not exist
                transaction.rollback();
                return Collections.emptyList();
            }

            if (dto.isEmpty()) { // question not exist
                transaction.rollback();
                return null;
            }

            transaction.commit();
        }

        comments = CommentQuestionQueryManager.dtoToCommentQuestionList(dto);
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

    private void setLikes(List<CommentQuestion> comments, long userId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            this.likesProvider.provide(comments, userId, jedis);
        }
    }
}
