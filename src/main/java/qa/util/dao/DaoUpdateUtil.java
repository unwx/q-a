package qa.util.dao;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import qa.dao.HqlBuilder;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldExtractor;
import qa.dao.databasecomponents.Where;

import java.util.Arrays;

public class DaoUpdateUtil<Entity extends FieldExtractor> {

    private final HqlBuilder hqlBuilder;

    public DaoUpdateUtil(HqlBuilder hqlBuilder) {
        this.hqlBuilder = hqlBuilder;
    }

    public void update(Where where, Entity entity, String className, Session session) {
        ImmutablePair<String, Field[]> pair = hqlBuilder.update(where, entity, className);
        updateProcess(pair.getLeft(), pair.getRight(), where.getFieldValue(), session);
    }


    private void updateProcess(String hql, Field[] params, Object param, Session session) {
        Query<?> query = session.createQuery(hql);
        setParams(query, params);
        Transaction transaction = session.beginTransaction();
        query.setParameter("a", param).executeUpdate();
        transaction.commit();
    }

    private void setParams(Query<?> query, Field[] params) {
        Arrays.stream(params).forEach((f) -> query.setParameter(f.getName(), f.getValue()));
    }
}
