package qa.util.dao;

import org.hibernate.Session;
import qa.dao.HqlBuilder;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldExtractor;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;

import java.util.List;

public class DaoUtilImpl<Entity extends FieldExtractor> implements DaoUtil<Entity> {

    private final DaoReadUtil<Entity> daoReadUtil;
    private final DaoUpdateUtil<Entity> daoUpdateUtil;

    public DaoUtilImpl(Class<Entity> clz) {
        HqlBuilder<Entity> hqlBuilder = new HqlBuilder<>();
        daoReadUtil = new DaoReadUtil<>(hqlBuilder, clz);
        daoUpdateUtil = new DaoUpdateUtil<>(hqlBuilder, clz);
    }

    @Override
    public Entity read(final Field where, final Table target, final List<NestedEntity> nested, final Session session) {
        return daoReadUtil.read(where, target, nested, session);
    }

    @Override
    public List<Entity> readList(final Field where, final Table target, final List<NestedEntity> nested, final Session session) {
        return daoReadUtil.readList(where, target, nested, session);
    }

    @Override
    public void update(final Field where, final Entity entity, final String className, final Session session) {
        daoUpdateUtil.update(where, entity, className, session);
    }
}
