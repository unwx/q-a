package qa.util.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import qa.dao.HqlBuilder;
import qa.dao.databasecomponents.*;
import qa.domain.setters.PropertySetter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DaoUtilImpl<Entity extends FieldExtractor & FieldDataSetterExtractor> implements DaoUtil<Entity> {

    private final DaoReadUtil<Entity> daoReadUtil;
    private final DaoUpdateUtil<Entity> daoUpdateUtil;

    private final Logger logger = LogManager.getLogger(DaoUtilImpl.class);

    public DaoUtilImpl(Entity emptyEntity, PropertySetter propertySetter) {
        HqlBuilder hqlBuilder = new HqlBuilder();
        daoReadUtil = new DaoReadUtil<>(hqlBuilder, emptyEntity, propertySetter);
        daoUpdateUtil = new DaoUpdateUtil<>(hqlBuilder);
    }

    @Override
    public Entity read(final Where where, final Table target, final List<NestedEntity> nested, final Session session) {
        try {
            return daoReadUtil.read(where, target, nested, session);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            logger.error("[read error]: make sure your model has setter.class & implemented FieldDataSetterExtractor and that your data is correct");
            return null;
        }
    }

    @Override
    public List<Entity> readList(final Where where, final Table target, final Session session) {
        return daoReadUtil.readList(where, target, session);
    }

    @Override
    public void update(final Where where, final Entity entity, final String className, final Session session) {
        daoUpdateUtil.update(where, entity, className, session);
    }
}
