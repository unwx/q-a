package qa.dao.daoutil;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.jetbrains.annotations.Nullable;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldExtractor;
import qa.dao.databasecomponents.Table;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DaoUtilImpl<Entity extends FieldExtractor> implements DaoUtil<Entity> {

    private final SessionFactory sessionFactory;
    private final HqlBuilder<Entity> hqlBuilder = new HqlBuilder<>();
    private final Class<Entity> clz;

    public DaoUtilImpl(SessionFactory sessionFactory,
                       Class<Entity> clz) {
        this.sessionFactory = sessionFactory;
        this.clz = clz;
    }

    @Override
    public Entity read(final Field where, final Table target, @Nullable List<Table> nested) {
        String hql = prepareForReading(where, target, nested);
        return readUniqueProcess(hql, where.getValue());
    }

    @Override
    public List<Entity> readList(final Field where, final Table target, @Nullable List<Table> nested) {
        String hql = prepareForReading(where, target, nested);
        return readListProcess(hql, where.getValue());
    }

    @Override
    public void update(Field where, Entity entity, String clz) {
        ImmutablePair<String, Field[]> pair = hqlBuilder.update(where, entity, clz);
        updateProcess(pair.getLeft(), pair.getRight());
    }

    private String prepareForReading(final Field where, final Table target, List<Table> nested) {
        if (nested == null)
            nested = Collections.emptyList();
        return hqlBuilder.read(where, target, nested);
    }

    private Entity readUniqueProcess(String hql, Object param) {
        ImmutablePair<Session, Query<Entity>> pair = beginSession(hql);
        Entity e = readUnique(pair.getRight(), param);
        commit(pair.left);
        return e;
    }

    private List<Entity> readListProcess(String hql, Object param) {
        ImmutablePair<Session, Query<Entity>> pair = beginSession(hql);
        List<Entity> entities = readList(pair.getRight(), param);
        commit(pair.left);
        return entities;
    }

    private void updateProcess(String hql, Field[] params) {
        try(Session session = sessionFactory.openSession()) {
            Query<Entity> query = session.createQuery(hql, clz);
            setParams(query, params);
            Transaction transaction = session.beginTransaction();
            query.executeUpdate();
            transaction.commit();
        }
    }

    private void setParams(Query<Entity> query, Field[] params) {
        Arrays.stream(params).forEach((f) -> query.setParameter(f.getName(), f.getValue()));
    }

    private ImmutablePair<Session, Query<Entity>> beginSession(String hql) {
        Session session = sessionFactory.openSession();
        Query<Entity> query = session.createQuery(hql, clz);
        session.beginTransaction();
        return new ImmutablePair<>(session, query);
    }

    private void commit(Session session) {
        session.getTransaction().commit();
        session.close();
    }

    private Entity readUnique(Query<Entity> query, Object param) {
        return query.setParameter("a", param).uniqueResult();
    }

    private List<Entity> readList(Query<Entity> query, Object param) {
        return query.setParameter("a", param).list();
    }
}
