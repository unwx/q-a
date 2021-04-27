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
import qa.cache.entity.like.provider.like.CommentQuestionLikeProvider;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.dao.query.CommentQuestionQueryCreator;
import qa.dao.query.convertor.CommentQuestionQueryResultConvertor;
import qa.domain.CommentQuestion;
import qa.domain.DomainName;
import qa.domain.setters.PropertySetterFactory;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryConfigurer;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommentQuestionDao extends DaoImpl<CommentQuestion> implements Likeable<Long> {

    private final SessionFactory sessionFactory;
    private final JedisResourceCenter jedisResourceCenter;
    private final CacheRemover cacheRemover;
    private final CommentQuestionLikeProvider likesProvider;

    @Autowired
    public CommentQuestionDao(PropertySetterFactory propertySetterFactory,
                              SessionFactory sessionFactory,
                              JedisResourceCenter jedisResourceCenter,
                              CacheRemover cacheRemover,
                              CommentQuestionLikeProvider likesProvider) {
        super(HibernateSessionFactoryConfigurer.getSessionFactory(), new CommentQuestion(), propertySetterFactory.getSetter(new CommentQuestion()));
        this.sessionFactory = sessionFactory;
        this.jedisResourceCenter = jedisResourceCenter;
        this.cacheRemover = cacheRemover;
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
        return super.isExist(new Where("id", id, WhereOperator.EQUALS), "Comment");
    }

    @Nullable
    public List<CommentQuestion> getComments(long questionId, long userId, int page) {

        /*
         *  if question not exist: comments.size() = 0; (NullResultException will not be thrown) - return null
         *  if comments not exist: NullResultException - return empty list.
         *  if exist: return result.
         */

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<CommentQuestion> comments = new ArrayList<>();

            try {
                comments = CommentQuestionQueryResultConvertor
                        .dtoToCommentQuestionList(CommentQuestionQueryCreator
                                .commentsQuery(session, questionId, page)
                                .list()
                        );
            } catch (NullResultException ex) { // comments not exist
                transaction.rollback();
                return comments;
            }

            if (comments.isEmpty()) { // question not exist
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
            this.cacheRemover.remove(DomainName.COMMENT_QUESTION, commentIdStr, jedis);
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
