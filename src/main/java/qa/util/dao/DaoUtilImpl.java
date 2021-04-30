package qa.util.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import qa.dao.Domain;
import qa.dao.database.components.*;
import qa.domain.setters.PropertySetter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DaoUtilImpl<E extends FieldExtractor & FieldDataSetterExtractor & Domain> implements DaoUtil<E> {

    private final DaoReadUtil<E> daoReadUtil;
    private final DaoUpdateUtil<E> daoUpdateUtil;
    private final DaoDeleteUtil daoDeleteUtil;
    private final DaoExistUtil<E> daoExistUtil;
    private final String entityClassName;

    private static final String ERR_PROPERTY_SETTER =
            """
            make sure your model has setter.class &\s\
            implemented FieldDataSetterExtractor and that your data is correct";\
            """;

    private static final Logger logger = LogManager.getLogger(DaoUtilImpl.class);

    public DaoUtilImpl(PropertySetter propertySetter) {
        this.daoReadUtil = new DaoReadUtil<>(propertySetter);
        this.daoUpdateUtil = new DaoUpdateUtil<>();
        this.daoDeleteUtil = new DaoDeleteUtil();
        this.daoExistUtil = new DaoExistUtil<>();
        this.entityClassName = propertySetter.entity().getClassName();
    }

    @Override
    public E read(final Where where, final Table target, final List<NestedEntity> nested, final Session session) {
        try {
            return this.daoReadUtil.read(where, target, nested, session);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            logger.error(ERR_PROPERTY_SETTER);
            return null;
        }
    }

    @Override
    public List<E> readList(final Where where, final Table target, final Session session) {
        return this.daoReadUtil.readList(where, target, session);
    }

    @Override
    public void update(final Where where, final E entity, final Session session) {
        this.daoUpdateUtil.update(where, entity, session);
    }

    @Override
    public void delete(Where where, Session session) {
        this.daoDeleteUtil.delete(where, entityClassName, session);
    }

    @Override
    public boolean isExist(Where where, Session session) {
        return this.daoExistUtil.isExist(where, entityClassName, session);
    }
}
