package qa.util.dao;

import org.hibernate.Session;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;

import java.util.List;

public interface DaoUtil<Entity> {

    Entity read(final Field where, final Table target, final List<NestedEntity> nested, final Session session);

    List<Entity> readList(final Field where, final Table target, final List<NestedEntity> nested, final Session session);

    void update(final Field where, final Entity entity, final String className, final Session session);
}
