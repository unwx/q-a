package qa.domain.setters;

import qa.dao.databasecomponents.FieldDataSetterExtractor;

public interface PropertySetter {
    void setAll(FieldDataSetterExtractor object, String[] names, Object[] values);

    void set(FieldDataSetterExtractor object, String name, Object value);
}
