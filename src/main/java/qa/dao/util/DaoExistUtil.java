package qa.dao.util;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import qa.dao.HqlBuilder;
import qa.dao.database.components.FieldDataSetterExtractor;
import qa.dao.database.components.FieldExtractor;
import qa.dao.database.components.Where;

public class DaoExistUtil<Entity extends FieldExtractor & FieldDataSetterExtractor> {

    private final HqlBuilder hqlBuilder = new HqlBuilder();

    public boolean isExist(Where where, String className, Session session) {
        final String hql = hqlBuilder.exist(className, where);
        final Query<?> query = session.createQuery(hql).setParameter("a", where.getFieldValue());

        final Transaction transaction = session.beginTransaction();
        final Object result = query.uniqueResult();

        transaction.commit();
        return result != null;
    }
}
