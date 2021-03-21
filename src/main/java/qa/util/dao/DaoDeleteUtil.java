package qa.util.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import qa.dao.HqlBuilder;
import qa.dao.databasecomponents.Where;

public class DaoDeleteUtil {

    private final HqlBuilder hqlBuilder;

    public DaoDeleteUtil(HqlBuilder hqlBuilder) {
        this.hqlBuilder = hqlBuilder;
    }

    public void delete(String className, Where where, Session session) {
        Transaction transaction = session.beginTransaction();
        session
                .createQuery(hqlBuilder.delete(className, where))
                .setParameter(hqlBuilder.DEFAULT_WHERE_PARAM_NAME, where.getFieldValue())
                .executeUpdate();
        transaction.commit();
    }
}
