package qa.validators.entities;

import qa.validators.abstraction.ValidationNestedField;

public class ValidationObjectField implements ValidationNestedField {
    private final Object obj;

    public ValidationObjectField(Object obj) {
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
    }

    @Override
    public Object[] getNested() {
        return new Object[]{
                obj
        };
    }
}
