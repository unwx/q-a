package qa.dao.database.components;

public class Where {

    private final String fieldName;
    private final Object fieldValue;
    private final WhereOperator operator;

    public Where(String fieldName,
                 Object fieldValue,
                 WhereOperator operator) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.operator = operator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public WhereOperator getOperator() {
        return operator;
    }
}
