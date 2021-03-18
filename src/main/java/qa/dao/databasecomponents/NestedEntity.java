package qa.dao.databasecomponents;

import qa.domain.setters.PropertySetter;

/**
 * one to one or many to one only
 */
public class NestedEntity implements EntityTable {

    private final String[] fieldNames;
    private final Class<? extends FieldDataSetterExtractor> clazz;
    private final PropertySetter propertySetter;

    public NestedEntity(String[] fieldNames,
                        Class<? extends FieldDataSetterExtractor> clazz,
                        PropertySetter propertySetter) {
        this.fieldNames = fieldNames;
        this.clazz = clazz;
        this.propertySetter = propertySetter;
    }

    @Override
    public String[] getFieldNames() {
        return fieldNames;
    }

    public Class<? extends FieldDataSetterExtractor> getClazz() {
        return clazz;
    }

    public PropertySetter getDomainSetter() {
        return propertySetter;
    }

    public String getNestedEntityName() {
        String name = clazz.getSimpleName();
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
