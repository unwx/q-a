package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import qa.util.dao.DaoUtil;
import qa.util.dao.DaoUtilImpl;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldExtractor;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.Collections;
import java.util.List;

final class DaoImpl<Entity extends FieldExtractor> implements Dao<Entity, Object> {

    private final SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    private final DaoUtil<Entity> daoUtil;


    public DaoImpl(Class<Entity> clz) {
        daoUtil = new DaoUtilImpl<>(clz);
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
        try(Session session = sessionFactory.openSession()) {
            return daoUtil.read(where, target, Collections.emptyList(), session);
        }
    }

    @Override
    @Nullable
    public Entity read(final Field where, final Table target, final List<NestedEntity> nested) {
        try(Session session = sessionFactory.openSession()) {
            return daoUtil.read(where, target, nested, session);
        }
    }

    @Override
    public List<Entity> readMany(final Field where, final Table target) {
        try(Session session = sessionFactory.openSession()) {
            return daoUtil.readList(where, target, Collections.emptyList(), session);
        }
    }

    @Override
    public List<Entity> readMany(final Field where, final Table target, final List<NestedEntity> nested) {
        try(Session session = sessionFactory.openSession()) {
            return daoUtil.readList(where, target, nested, session);
        }
    }

    @Override
    public void update(Field where, Entity entity, String className) {
        try(Session session = sessionFactory.openSession()) {
            daoUtil.update(where, entity, className, session);
        }
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
