package qa.util.dao;

import org.hibernate.Session;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;

import java.util.List;

public interface DaoUtil<Entity> {

    Entity read(final Where where, final Table target, final List<NestedEntity> nested, final Session session);

    List<Entity> readList(final Where where, final Table target, final Session session);

    void update(final Where where, final Entity entity, final Session session);

    void delete(final String className, final Where where, final Session session);
}
