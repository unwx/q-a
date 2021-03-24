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

class DaoImpl<Entity extends FieldExtractor & FieldDataSetterExtractor> implements Dao<Entity, Object> {

    private final SessionFactory sessionFactory;
    private final DaoUtil<Entity> daoUtil;


    public DaoImpl(SessionFactory sessionFactory,
                   Entity emptyEntity,
                   PropertySetter propertySetter) {
        this.sessionFactory = sessionFactory;
        daoUtil = new DaoUtilImpl<>(emptyEntity, propertySetter);
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
        try(Session session = sessionFactory.openSession()) {
            return daoUtil.read(where, target, Collections.emptyList(), session);
        }
    }

    @Override
    @Nullable
    public Entity read(final Where where, final Table target, final List<NestedEntity> nested) {
        try(Session session = sessionFactory.openSession()) {
            return daoUtil.read(where, target, nested, session);
        }
    }

    @Override
    public List<Entity> readMany(final Where where, final Table target) {
        try(Session session = sessionFactory.openSession()) {
            return daoUtil.readList(where, target, session);
        }
    }

    @Override
    public void update(final Where where, final Entity entity, final String className) {
        try(Session session = sessionFactory.openSession()) {
            daoUtil.update(where, entity, className, session);
        }
    }

    @Override
    public void updateEager(final Entity entity) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
        }
    }

    @Override
    public void delete(final Class<Entity> clazz, final Where where) {
        try(Session session = sessionFactory.openSession()) {
            daoUtil.delete(clazz.getName(), where, session);
        }
    }
}
