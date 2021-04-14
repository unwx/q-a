package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.dao.query.CommentAnswerQueryCreator;
import qa.dao.query.convertor.CommentAnswerQueryResultConvertor;
import qa.domain.CommentAnswer;
import qa.domain.setters.PropertySetterFactory;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommentAnswerDao extends DaoImpl<CommentAnswer> {

    private final SessionFactory sessionFactory;

    @Autowired
    public CommentAnswerDao(PropertySetterFactory propertySetterFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new CommentAnswer(), propertySetterFactory.getSetter(new CommentAnswer()));
        this.sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    }

    @Override
    public Long create(CommentAnswer e) {
        return (Long) super.create(e);
    }

    public boolean isExist(Long id) {
        return super.isExist(new Where("id", id, WhereOperator.EQUALS), "Comment");
    }

    @Nullable
    public List<CommentAnswer> getComments(long answerId, int page) {

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
            return comments;
        }
    }
}
