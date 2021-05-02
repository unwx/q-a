package qa.dao.util;

import org.hibernate.Session;
import qa.dao.database.components.NestedEntity;
import qa.dao.database.components.Table;
import qa.dao.database.components.Where;

import java.util.List;

public interface DaoUtil<Entity> {

    Entity read(final Where where, final Table target, final List<NestedEntity> nested, final Session session);

    List<Entity> readList(final Where where, final Table target, final Session session);

    void update(final Where where, final Entity entity, final Session session);

    void delete(final Where where, final Session session);

    boolean isExist(final Where where, final Session session);
}
