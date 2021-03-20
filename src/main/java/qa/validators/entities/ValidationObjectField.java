package qa.validators.entities;

import qa.validators.abstraction.ValidationField;

public class ValidationObjectField implements ValidationField {
    private final Object obj;

    public ValidationObjectField(Object obj) {
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
    }

    @Override
    public Object[] getField() {
        return new Object[]{
                obj
        };
    }
}
