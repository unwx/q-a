package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.cache.entity.like.LikesUtil;
import qa.cache.operation.impl.CommentQuestionToLikeSetOperation;
import qa.cache.operation.impl.UserToCommentQuestionLikeSetOperation;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.dao.query.CommentQuestionQueryCreator;
import qa.dao.query.convertor.CommentQuestionQueryResultConvertor;
import qa.domain.CommentQuestion;
import qa.domain.setters.PropertySetterFactory;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryUtil;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommentQuestionDao extends DaoImpl<CommentQuestion> implements Likeable<Long> {

    private final SessionFactory sessionFactory;
    private final JedisResourceCenter jedisResourceCenter;

    @Autowired
    public CommentQuestionDao(PropertySetterFactory propertySetterFactory,
                              JedisResourceCenter jedisResourceCenter) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new CommentQuestion(), propertySetterFactory.getSetter(new CommentQuestion()));
        this.jedisResourceCenter = jedisResourceCenter;
        this.sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    }

    @Override
    public Long create(CommentQuestion e) {
        final Long id = (Long) super.create(e);
        this.createLike(id);
        return id;
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
    public void like(long userId, Long id) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            final CommentQuestionToLikeSetOperation answerToLikeSetOperation = new CommentQuestionToLikeSetOperation(jedis);
            final UserToCommentQuestionLikeSetOperation userToAnswerLikeSetOperation = new UserToCommentQuestionLikeSetOperation(jedis);

            final boolean status = userToAnswerLikeSetOperation.add(userId, id);
            if (status) answerToLikeSetOperation.increment(id);
        }
    }


    private void createLike(long commentId) {
        try(JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            final CommentQuestionToLikeSetOperation operation = new CommentQuestionToLikeSetOperation(jedis);
            LikesUtil.createLike(commentId, operation);
        }
    }

    private void setLikes(List<CommentQuestion> comments, long userId) {
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            final CommentQuestionToLikeSetOperation commentSetOperation = new CommentQuestionToLikeSetOperation(jedis);
            final UserToCommentQuestionLikeSetOperation userToCommentSetOperation = new UserToCommentQuestionLikeSetOperation(jedis);
            LikesUtil.setLikesAndLiked(comments, userId, commentSetOperation, userToCommentSetOperation);
        }
    }
}
