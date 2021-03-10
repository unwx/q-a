package qa.dao.databasecomponents;

import java.util.List;

/**
 * <h1>class name should be lowercase;</h1>
 */
public class NestedEntity implements EntityTable {

    private final List<String> fieldNames;
    private final String className;

    public NestedEntity(List<String> fieldNames, String className) {
        this.fieldNames = fieldNames;
        this.className = className;
    }

    @Override
    public List<String> getFieldNames() {
        return fieldNames;
    }

    public String getClassName() {
        return className;
    }
}
