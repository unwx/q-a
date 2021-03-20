package qa.validators.entities;

import qa.validators.abstraction.ValidationField;

/**
 * <h3>class null = ignore</h3>
 * <h3>field '-1' = ignore</h3>
 */
public class ValidationNumberField implements ValidationField {
    private final Long num;
    private final Long max;
    private final Long min;

    public ValidationNumberField(Long num, Long max, Long min) {
        this.num = num;
        this.max = max;
        this.min = min;
    }

    public Long getNum() {
        return num;
    }

    public Long getMax() {
        return max;
    }

    public Long getMin() {
        return min;
    }

    @Override
    public Object[] getField() {
        return new Object[]{
                num, max, min
        };
    }
}
