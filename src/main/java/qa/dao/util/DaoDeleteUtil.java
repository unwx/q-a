package qa.dao.util;

import org.hibernate.Session;
import org.hibernate.Transaction;
import qa.dao.HqlBuilder;
import qa.dao.database.components.Where;

public class DaoDeleteUtil {

    private final HqlBuilder hqlBuilder = new HqlBuilder();

    public void delete(Where where,String className, Session session) {
        final Transaction transaction = session.beginTransaction();
        session
                .createQuery(hqlBuilder.delete(className, where))
                .setParameter(hqlBuilder.DEFAULT_WHERE_PARAM_NAME, where.getFieldValue())
                .executeUpdate();
        transaction.commit();
    }
}
