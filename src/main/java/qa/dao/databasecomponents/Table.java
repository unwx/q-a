package qa.dao.databasecomponents;

import java.util.List;

/**
 * <h3>USER IN HQL BUILDER.</h3>
 * <h2>WARN:</h2> <u><strong>for target table clz = 'Classname'; for NESTED tables clz = 'classname'!</strong></u>
 */
public class Table {
    private final List<String> fields;
    private final String clz;

    public Table(List<String> fields, String clz) {
        this.fields = fields;
        this.clz = clz;
    }

    public List<String> getFields() {
        return fields;
    }

    public String getClz() {
        return clz;
    }
}
