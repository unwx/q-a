package qa.dao;

import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;
import qa.domain.Question;
import qa.domain.setters.QuestionSetter;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.List;

@Component
public class QuestionDao implements Dao<Question, Long> {

    SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final DaoImpl<Question> dao = new DaoImpl<>(
            sessionFactory,
            new Question(),
            QuestionSetter.getInstance()
    );

    @Override
    public Long create(@NotNull final Question question) {
        return (Long) dao.create(question);
    }

    @Override
    public @Nullable Question read(@NotNull final Where where, @NotNull final Table target) {
        return dao.read(where, target);
    }

    @Override
    public @Nullable Question read(@NotNull final Where where, @NotNull final Table target, @NotNull final List<NestedEntity> nested) {
        return dao.read(where, target, nested);
    }

    @Override
    public List<Question> readMany(@NotNull final Where where, @NotNull final Table target) {
        return dao.readMany(where, target);
    }

    @Override
    public void update(@NotNull final Where where, @NotNull final Question question, @NotNull final String className) {
        dao.update(where, question, className);
    }

    @Override
    public void updateEager(@NotNull final Question question) {
        dao.updateEager(question);
    }

    @Override
    public void delete(@NotNull final Question question) {
        dao.delete(question);
    }
}
