package qa.dao;

import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;
import qa.domain.Answer;
import qa.domain.setters.AnswerSetter;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.List;

@Component
public class AnswerDao implements Dao<Answer, Long> {

    SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final DaoImpl<Answer> dao = new DaoImpl<>(
            sessionFactory,
            new Answer(),
            AnswerSetter.getInstance()
    );

    @Override
    public Long create(@NotNull final Answer answer) {
        return (Long) dao.create(answer);
    }

    @Override
    public @Nullable Answer read(@NotNull final Where where, @NotNull final Table target) {
        return dao.read(where, target);
    }

    @Override
    public @Nullable Answer read(@NotNull final Where where, @NotNull final Table target, @NotNull final List<NestedEntity> nested) {
        return dao.read(where, target, nested);
    }

    @Override
    public List<Answer> readMany(@NotNull final Where where, @NotNull final Table target) {
        return dao.readMany(where, target);
    }

    @Override
    public void update(@NotNull final Where where, @NotNull final Answer answer, @NotNull final String className) {
        dao.update(where, answer, className);
    }

    @Override
    public void updateEager(@NotNull final Answer answer) {
        dao.updateEager(answer);
    }

    @Override
    public void delete(@NotNull final Answer answer) {
        dao.delete(answer);
    }
}
