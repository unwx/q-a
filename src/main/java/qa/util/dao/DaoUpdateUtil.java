package qa.util.dao;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import qa.dao.HqlBuilder;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldExtractor;

import java.util.Arrays;

public class DaoUpdateUtil<Entity extends FieldExtractor> {

    private final HqlBuilder<Entity> hqlBuilder;
    private final Class<Entity> clz;

    public DaoUpdateUtil(HqlBuilder<Entity> hqlBuilder,
                         Class<Entity> clz) {
        this.hqlBuilder = hqlBuilder;
        this.clz = clz;
    }

    public void update(Field where, Entity entity, String className, Session session) {
        ImmutablePair<String, Field[]> pair = hqlBuilder.update(where, entity, className);
        updateProcess(pair.getLeft(), pair.getRight(), session);
    }


    private void updateProcess(String hql, Field[] params, Session session) {
        Query<Entity> query = session.createQuery(hql, clz);
        setParams(query, params);
        Transaction transaction = session.beginTransaction();
        query.executeUpdate();
        transaction.commit();
    }

    private void setParams(Query<Entity> query, Field[] params) {
        Arrays.stream(params).forEach((f) -> query.setParameter(f.getName(), f.getValue()));
    }
}
