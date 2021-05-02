package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import qa.dao.database.components.*;
import qa.dao.util.DaoUtilImpl;
import qa.domain.setters.PropertySetter;

import java.util.Collections;
import java.util.List;

abstract class DaoImpl<E extends FieldExtractor & FieldDataSetterExtractor> extends DaoUtilImpl<E> implements Dao<E, Object> {

    private final SessionFactory sessionFactory;

    protected DaoImpl(SessionFactory sessionFactory,
                      PropertySetter propertySetter) {
        super(propertySetter);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Object create(final E e) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            final Object id = session.save(e);

            transaction.commit();
            return id;
        }
    }

    @Override
    @Nullable
    public E read(final Where where, final Table target) {
        try (Session session = sessionFactory.openSession()) {
            return super.read(where, target, Collections.emptyList(), session);
        }
    }

    @Override
    @Nullable
    public E read(final Where where, final Table target, final List<NestedEntity> nested) {
        try (Session session = sessionFactory.openSession()) {
            return super.read(where, target, nested, session);
        }
    }

    @Override
    public List<E> readMany(final Where where, final Table target) {
        try (Session session = sessionFactory.openSession()) {
            return super.readList(where, target, session);
        }
    }

    @Override
    public void update(final Where where, final E entity) {
        try (Session session = sessionFactory.openSession()) {
            super.update(where, entity, session);
        }
    }

    @Override
    public void updateEager(final E entity) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
        }
    }

    @Override
    public void delete(final Where where) {
        try (Session session = sessionFactory.openSession()) {
            super.delete(where, session);
        }
    }

    @Override
    public boolean isExist(final Where where) {
        try (Session session = sessionFactory.openSession()) {
            return super.isExist(where, session);
        }
    }
}
