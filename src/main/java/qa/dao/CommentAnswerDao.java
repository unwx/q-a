package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.CacheRemover;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.cache.entity.like.provider.CommentAnswerCacheProvider;
import qa.cache.entity.like.provider.like.CommentAnswerLikeProvider;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.dao.query.CommentAnswerQueryCreator;
import qa.dao.query.convertor.CommentAnswerQueryResultConvertor;
import qa.domain.CommentAnswer;
import qa.domain.DomainName;
import qa.domain.setters.PropertySetterFactory;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryConfigurer;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommentAnswerDao extends DaoImpl<CommentAnswer> implements Likeable<Long> {

    private final SessionFactory sessionFactory;
    private final JedisResourceCenter jedisResourceCenter;
    private final CacheRemover cacheRemover;
    private final CommentAnswerCacheProvider cacheProvider;
    private final CommentAnswerLikeProvider likesProvider;


    @Autowired
    public CommentAnswerDao(PropertySetterFactory propertySetterFactory,
                            SessionFactory sessionFactory,
                            JedisResourceCenter jedisResourceCenter,
                            CacheRemover cacheRemover,
                            CommentAnswerCacheProvider cacheProvider,
                            CommentAnswerLikeProvider likesProvider) {
        super(HibernateSessionFactoryConfigurer.getSessionFactory(), new CommentAnswer(), propertySetterFactory.getSetter(new CommentAnswer()));
        this.sessionFactory = sessionFactory;
        this.jedisResourceCenter = jedisResourceCenter;
        this.cacheRemover = cacheRemover;
        this.cacheProvider = cacheProvider;
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
        return super.isExist(new Where("id", id, WhereOperator.EQUALS), "Comment");
    }

    @Nullable
    public List<CommentAnswer> getComments(long answerId, long userId, int page) {

        /*
         *  if answer not exist: answers.size() = 0; (NullResultException will not be thrown) - return null
         *  if comments not exist: NullResultException - return empty list.
         *  if exist: return result.
         */

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<CommentAnswer> comments = new ArrayList<>();

            try {
                comments = CommentAnswerQueryResultConvertor
                        .dtoToCommentAnswerList(CommentAnswerQueryCreator
                                .commentsQuery(session, answerId, page)
                                .list()
                        );
            }
            catch (NullResultException ex) {
                transaction.rollback();
                return comments;
            }

            if (comments.isEmpty()) {
                transaction.rollback();
                return null;
            }

            transaction.commit();
            setLikes(comments, userId);
            return comments;
        }
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
            final String commentIdStr = String.valueOf(commentId);
            this.cacheRemover.remove(DomainName.COMMENT_ANSWER, commentIdStr, jedis);
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
            this.cacheProvider.provide(comments, userId, jedis);
        }
    }
}
