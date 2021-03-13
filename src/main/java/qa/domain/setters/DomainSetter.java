package qa.domain.setters;

import qa.dao.databasecomponents.FieldDataSetterExtractor;

public interface DomainSetter<T extends FieldDataSetterExtractor> {
    void setAll(T object, String[] names, Object[] values);

    void set(T object, String name, Object value);
}
