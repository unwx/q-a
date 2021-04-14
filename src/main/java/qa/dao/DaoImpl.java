package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import qa.dao.databasecomponents.*;
import qa.domain.setters.PropertySetter;
import qa.util.dao.DaoUtil;
import qa.util.dao.DaoUtilImpl;

import java.util.Collections;
import java.util.List;

abstract class DaoImpl<Entity extends FieldExtractor & FieldDataSetterExtractor> implements Dao<Entity, Object> {

    private final SessionFactory sessionFactory;
    private final DaoUtil<Entity> daoUtil;
    private final Entity emptyEntity;

    protected DaoImpl(SessionFactory sessionFactory,
                      Entity emptyEntity,
                      PropertySetter propertySetter) {
        this.sessionFactory = sessionFactory;
        this.daoUtil = new DaoUtilImpl<>(emptyEntity, propertySetter);
        this.emptyEntity = emptyEntity;
    }

    @Override
    public Object create(final Entity e) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Object id = session.save(e);
            transaction.commit();
            return id;
        }
    }

    @Override
    @Nullable
    public Entity read(final Where where, final Table target) {
        try (Session session = sessionFactory.openSession()) {
            return daoUtil.read(where, target, Collections.emptyList(), session);
        }
    }

    @Override
    @Nullable
    public Entity read(final Where where, final Table target, final List<NestedEntity> nested) {
        try (Session session = sessionFactory.openSession()) {
            return daoUtil.read(where, target, nested, session);
        }
    }

    @Override
    public List<Entity> readMany(final Where where, final Table target) {
        try (Session session = sessionFactory.openSession()) {
            return daoUtil.readList(where, target, session);
        }
    }

    @Override
    public void update(final Where where, final Entity entity) {
        try (Session session = sessionFactory.openSession()) {
            daoUtil.update(where, entity, session);
        }
    }

    @Override
    public void updateEager(final Entity entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
        }
    }

    @Override
    public void delete(final Where where) {
        try (Session session = sessionFactory.openSession()) {
            daoUtil.delete(emptyEntity.getClass().getSimpleName(), where, session);
        }
    }

    @Override
    public boolean isExist(final Where where, String className) {
        try (Session session = sessionFactory.openSession()) {
            return daoUtil.isExist(where, className, session);
        }
    }
}
