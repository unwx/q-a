package qa.dao;

import org.jetbrains.annotations.Nullable;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;

import java.util.List;

/**
 * <b>create:</b>
 * creates an entity based on ALL fields
 * <p></p>
 * <b>read / readList</b>
 * read fields which you indicated.
 * unique result: return null on unsuccessful search
 * result list: return EMPTY LIST on unsuccessful search
 * <p></p>
 * <b>update:</b>
 * updates the fields of the entity
 * <p></p>
 * <b>delete:</b>
 * removes entity (removes orphans by parameter orphanRemoval)
 */
public interface Dao<Entity, Key> {
    Key create(final Entity entity);

    @Nullable
    Entity read(final Where where, final Table target);

    @Nullable
    Entity read(final Where where, final Table target, final List<NestedEntity> nested);

    List<Entity> readMany(final Where where, final Table target);

    void update(final Where where, final Entity entity);

    void updateEager(final Entity entity);

    void delete(final Where where);
}