package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.dao.query.CommentQuestionQueryCreator;
import qa.dao.query.convertor.CommentQuestionQueryResultConvertor;
import qa.domain.CommentQuestion;
import qa.domain.setters.PropertySetterFactory;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommentQuestionDao extends DaoImpl<CommentQuestion> {

    private final SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();

    @Autowired
    public CommentQuestionDao(PropertySetterFactory propertySetterFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new CommentQuestion(), propertySetterFactory.getSetter(new CommentQuestion()));
    }

    @Override
    public Long create(CommentQuestion e) {
        return (Long) super.create(e);
    }

    public boolean isExist(Long id) {
        return super.isExist(new Where("id", id, WhereOperator.EQUALS), "Comment");
    }

    @Nullable
    public List<CommentQuestion> getComments(long questionId, int page) {

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
            return comments;
        }
    }
}
