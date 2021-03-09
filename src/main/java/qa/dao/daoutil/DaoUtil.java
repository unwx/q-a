package qa.dao.daoutil;

import org.jetbrains.annotations.Nullable;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.Table;

import java.util.List;

public interface DaoUtil<Entity> {

    Entity read(final Field where, final Table target, @Nullable final List<Table> nested);

    List<Entity> readList(final Field where, final Table target, @Nullable final List<Table> nested);

    void update(final Field where, final Entity entity, final String clz);
}
