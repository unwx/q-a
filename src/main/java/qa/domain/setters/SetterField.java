package qa.domain.setters;

public class SetterField {
    private final String name;
    private final Class<?> type;

    public SetterField(String name,
                       Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }
}
