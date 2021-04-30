package qa.dao.database.components;

public enum WhereOperator {

    GREATER(">"),
    LESS("<"),
    EQUALS("=");

    public final String label;

    WhereOperator(String label) {
        this.label = label;
    }
}
