package qa.dao.databasecomponents;

import qa.domain.setters.DomainSetter;

/**
 * be careful that the field name matches the class name (with a lowercase letter)
 */
public class NestedEntity implements EntityTable {

    private final String[] fieldNames;
    private final Class<? extends FieldDataSetterExtractor> clazz;
    private final DomainSetter<FieldDataSetterExtractor> domainSetter;

    public NestedEntity(String[] fieldNames,
                        Class<? extends FieldDataSetterExtractor> clazz,
                        DomainSetter<FieldDataSetterExtractor> domainSetter) {
        this.fieldNames = fieldNames;
        this.clazz = clazz;
        this.domainSetter = domainSetter;
    }

    @Override
    public String[] getFieldNames() {
        return fieldNames;
    }

    public Class<? extends FieldDataSetterExtractor> getClazz() {
        return clazz;
    }

    public DomainSetter<FieldDataSetterExtractor> getDomainSetter() {
        return domainSetter;
    }

    public String getNestedEntityName() {
        String name = clazz.getName();
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
