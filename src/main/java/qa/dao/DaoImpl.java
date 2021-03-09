package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import qa.dao.daoutil.DaoUtil;
import qa.dao.daoutil.DaoUtilImpl;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldExtractor;
import qa.dao.databasecomponents.Table;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.List;

final class DaoImpl<Entity extends FieldExtractor> implements Dao<Entity, Object> {

    private final SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final DaoUtil<Entity> daoUtil;


    public DaoImpl(Class<Entity> clz) {
        daoUtil = new DaoUtilImpl<>(sessionFactory, clz);
    }

    @Override
    public Object create(final @NotNull Entity e) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Object id = session.save(e);
            transaction.commit();
            return id;
        }
    }

    @Override
    @Nullable
    public Entity read(final Field where, final Table target) {
        return daoUtil.read(where, target, null);
    }

    @Override
    @Nullable
    public Entity read(final Field where, final Table target, final List<Table> nested) {
        return daoUtil.read(where, target, nested);
    }

    @Override
    public List<Entity> readMany(final Field where, final Table target) {
        return daoUtil.readList(where, target, null);
    }

    @Override
    public List<Entity> readMany(final Field where, final Table target, final List<Table> nested) {
        return daoUtil.readList(where, target, nested);
    }

    @Override
    public void update(Field where, Entity entity, String clz) {
        daoUtil.update(where, entity, clz);
    }

    @Override
    public void updateEager(Entity entity) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
        }
    }

    @Override
    public void delete(@NotNull Entity e) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(e);
            transaction.commit();
        }
    }
}
