package qa.util.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import qa.dao.HqlBuilder;
import qa.dao.databasecomponents.FieldDataSetterExtractor;
import qa.dao.databasecomponents.FieldExtractor;
import qa.dao.databasecomponents.Where;

public class DaoExistUtil<Entity extends FieldExtractor & FieldDataSetterExtractor> {

    private final HqlBuilder hqlBuilder;

    public DaoExistUtil(HqlBuilder hqlBuilder) {
        this.hqlBuilder = hqlBuilder;
    }

    public boolean isExist(Where where, String className, Session session) {
        String hql = hqlBuilder.exist(className, where);
        Query<?> query = session.createQuery(hql).setParameter("a", where.getFieldValue());
        Transaction transaction = session.beginTransaction();
        Object result = query.uniqueResult();
        transaction.commit();
        return result != null;
    }
}
