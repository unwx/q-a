package qa.util.dao;

import org.hibernate.Session;
import qa.dao.HqlBuilder;
import qa.dao.databasecomponents.*;
import qa.domain.setters.DomainSetter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DaoUtilImpl<Entity extends FieldExtractor & FieldDataSetterExtractor> implements DaoUtil<Entity> {

    private final DaoReadUtil<Entity> daoReadUtil;
    private final DaoUpdateUtil<Entity> daoUpdateUtil;
    private final Entity emptyEntity;

    public DaoUtilImpl(Entity emptyEntity,
                       DomainSetter<Entity> mainSetter) {
        this.emptyEntity = emptyEntity;
        HqlBuilder<Entity> hqlBuilder = new HqlBuilder<>();
        daoReadUtil = new DaoReadUtil<>(hqlBuilder, mainSetter);
        daoUpdateUtil = new DaoUpdateUtil<>(hqlBuilder);
    }

    @Override
    public Entity read(final Where where, final Table target, final List<NestedEntity> nested, final Session session) {
        try {
            return daoReadUtil.read(where, target, nested, session, emptyEntity);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("make sure your model has setter.class & implemented FieldDataSetterExtractor and that your data is correct");
            return null;
        }
    }

    @Override
    public List<Entity> readList(final Where where, final Table target, final Session session) {
        return daoReadUtil.readList(where, target, session, emptyEntity);
    }

    @Override
    public void update(final Where where, final Entity entity, final String className, final Session session) {
        daoUpdateUtil.update(where, entity, className, session);
    }
}
