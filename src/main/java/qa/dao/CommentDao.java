package qa.dao;

import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;
import qa.domain.Comment;
import qa.domain.setters.PropertySetterFactory;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.List;

@Component
public class CommentDao implements Dao<Comment, Long> {

    SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final DaoImpl<Comment> dao;

    @Autowired
    public CommentDao(PropertySetterFactory propertySetterFactory) {
        dao = new DaoImpl<>(
                sessionFactory,
                new Comment(),
                propertySetterFactory.getSetter(new Comment()));
    }

    @Override
    public Long create(@NotNull final Comment comment) {
        return (Long) dao.create(comment);
    }

    @Override
    public @Nullable Comment read(@NotNull final Where where, @NotNull final Table target) {
        return dao.read(where, target);
    }

    @Override
    public @Nullable Comment read(@NotNull final Where where, @NotNull final Table target, @NotNull final List<NestedEntity> nested) {
        return dao.read(where, target, nested);
    }

    @Override
    public List<Comment> readMany(@NotNull final Where where, @NotNull final Table target) {
        return dao.readMany(where, target);
    }

    @Override
    public void update(@NotNull final Where where, @NotNull final Comment comment, @NotNull final String className) {
        dao.update(where, comment, className);
    }

    @Override
    public void updateEager(@NotNull final Comment comment) {
        dao.updateEager(comment);
    }

    @Override
    public void delete(@NotNull final Comment comment) {
        dao.delete(comment);
    }
}
