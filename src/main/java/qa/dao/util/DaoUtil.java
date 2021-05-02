package qa.dao.util;

import org.hibernate.Session;
import qa.dao.database.components.*;

import java.util.List;

public interface DaoUtil<E extends FieldExtractor & FieldDataSetterExtractor> {

    E read(final Where where, final Table target, final List<NestedEntity> nested, final Session session);

    List<E> readList(final Where where, final Table target, final Session session);

    void update(final Where where, final E entity, final Session session);

    void delete(final Where where, final Session session);

    boolean isExist(final Where where, final Session session);
}