package qa.dao.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import qa.dao.HqlBuilder;
import qa.dao.database.components.Field;
import qa.dao.database.components.FieldExtractor;
import qa.dao.database.components.Where;

import java.util.Arrays;

public class DaoUpdateUtil<Entity extends FieldExtractor> {

    private final HqlBuilder hqlBuilder = new HqlBuilder();

    public void update(Where where, Entity entity, Session session) {
        final ImmutablePair<String, Field[]> pair = hqlBuilder.update(where, entity);
        this.updateProcess(pair.getLeft(), pair.getRight(), where.getFieldValue(), session);
    }


    private void updateProcess(String hql, Field[] params, Object param, Session session) {
        final Query<?> query = session.createQuery(hql);
        setParams(query, params);

        final Transaction transaction = session.beginTransaction();
        query.setParameter("a", param).executeUpdate();

        transaction.commit();
    }

    private void setParams(Query<?> query, Field[] params) {
        Arrays.stream(params).forEach((f) -> query.setParameter(f.getName(), f.getValue()));
    }
}