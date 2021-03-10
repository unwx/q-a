package qa.util.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.HqlBuilder;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldExtractor;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;

import java.util.List;

public class DaoReadUtil<Entity extends FieldExtractor> {

    private final HqlBuilder<Entity> hqlBuilder;
    private final Class<Entity> clz;

    public DaoReadUtil(HqlBuilder<Entity> hqlBuilder,
                       Class<Entity> clz) {
        this.hqlBuilder = hqlBuilder;
        this.clz = clz;
    }

    public Entity read(final Field where, final Table target, final List<NestedEntity> nested, final Session session) {
        String hql = hqlBuilder.read(where, target, nested);
        return readUniqueProcess(hql, where.getValue(), session);
    }

    public List<Entity> readList(final Field where, final Table target, final List<NestedEntity> nested, final Session session) {
        String hql = hqlBuilder.read(where, target, nested);
        return readListProcess(hql, where.getValue(), session);
    }

    private Entity readUniqueProcess(String hql, Object param,  Session session) {
        Query<Entity> query = createQuery(hql, session);
        session.beginTransaction();
        Entity e = readUnique(query, param);
        commit(session);
        return e;
    }

    private List<Entity> readListProcess(String hql, Object param,  Session session) {
        Query<Entity> query = createQuery(hql, session);
        session.beginTransaction();
        List<Entity> entities = readList(query, param);
        commit(session);
        return entities;
    }

    private Query<Entity> createQuery(String hql, Session session) {
        return session.createQuery(hql, clz);
    }

    private void commit(Session session) {
        session.getTransaction().commit();
    }

    private Entity readUnique(Query<Entity> query, Object param) {
        return query.setParameter(hqlBuilder.DEFAULT_WHERE_PARAM_NAME, param).uniqueResult();
    }

    private List<Entity> readList(Query<Entity> query, Object param) {
        return query.setParameter(hqlBuilder.DEFAULT_WHERE_PARAM_NAME, param).list();
    }
}
