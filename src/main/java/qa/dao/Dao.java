package qa.dao;

import org.jetbrains.annotations.Nullable;
import qa.dao.database.components.NestedEntity;
import qa.dao.database.components.Table;
import qa.dao.database.components.Where;

import java.util.List;

/**
 * <b>create:</b>
 * creates an entity
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
 * removes entity
 */
public interface Dao<E, Key> {
    Key create(final E entity);

    @Nullable
    E read(final Where where, final Table target);

    @Nullable
    E read(final Where where, final Table target, final List<NestedEntity> nested);

    List<E> readMany(final Where where, final Table target);

    void update(final Where where, final E entity);

    void updateEager(final E entity);

    void delete(final Where where);

    boolean isExist(final Where where);
}