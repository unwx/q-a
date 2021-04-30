package qa.domain.setters;

import qa.dao.Domain;
import qa.dao.database.components.FieldDataSetterExtractor;

public interface PropertySetter {
    void setAll(FieldDataSetterExtractor object, String[] names, Object[] values);

    void set(FieldDataSetterExtractor object, String name, Object value);

    <E extends FieldDataSetterExtractor & Domain> E entity();
}
