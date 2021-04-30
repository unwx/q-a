package qa.dao.database.components;

/**
 * <h1>className: value should be with a capital letter </h1>
 */
public class Table implements EntityTable {

    private final String[] fieldNames;
    private final String className;

    public Table(String[] fieldNames,
                 String className) {
        this.fieldNames = fieldNames;
        this.className = className;
    }

    @Override
    public String[] getFieldNames() {
        return fieldNames;
    }

    public String getClassName() {
        return className;
    }
}
