package qa.util.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import qa.dao.HqlBuilder;
import qa.dao.databasecomponents.FieldDataSetterExtractor;
import qa.dao.databasecomponents.FieldExtractor;
import qa.dao.databasecomponents.Where;

public class DaoExistUtil<Entity extends FieldExtractor & FieldDataSetterExtractor> {

    private final HqlBuilder hqlBuilder;
    private final Entity targetEntity;

    private final static Logger logger = LogManager.getLogger(DaoReadUtil.class);


    public DaoExistUtil(HqlBuilder hqlBuilder,
                        Entity targetEntity) {
        this.hqlBuilder = hqlBuilder;
        this.targetEntity = targetEntity;
    }

    public boolean isExist(Where where, Session session) {
        String hql = hqlBuilder.exist(targetEntity.getClass().getSimpleName(), where);
        Query<?> query = session.createQuery(hql).setParameter("a", where.getFieldValue());
        Transaction transaction = session.beginTransaction();
        Object result = query.uniqueResult();
        transaction.commit();
        return result != null;
    }
}
