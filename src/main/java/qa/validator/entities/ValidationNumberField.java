package qa.validator.entities;

import qa.validator.abstraction.ValidationField;

/**
 * <h3>class null = ignore</h3>
 * <h3>field '-1' = ignore</h3>
 */
public class ValidationNumberField implements ValidationField {
    private final Number num;
    private final Number max;
    private final Number min;

    public ValidationNumberField(Number num, Number max, Number min) {
        this.num = num;
        this.max = max;
        this.min = min;
    }

    public Number getNum() {
        return num;
    }

    public Number getMax() {
        return max;
    }

    public Number getMin() {
        return min;
    }

    @Override
    public Object[] getField() {
        return new Object[]{
                num, max, min
        };
    }
}
