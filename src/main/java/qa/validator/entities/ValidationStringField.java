package qa.validator.entities;

import qa.validator.abstraction.ValidationField;

/**
 * <h3>class null = ignore</h3>
 * <h3>field '-1' = ignore</h3>
 */
public class ValidationStringField implements ValidationField {
    private final String s;
    private final int minLen;
    private final int maxLen;

    public ValidationStringField(String s, int minLen, int maxLen) {
        this.s = s;
        this.minLen = minLen;
        this.maxLen = maxLen;
    }

    public String getS() {
        return s;
    }

    public int getMinLen() {
        return minLen;
    }

    public int getMaxLen() {
        return maxLen;
    }


    @Override
    public Object[] getField() {
        return new Object[]{
                s
        };
    }
}
