package qa.validator.entities;

import qa.validator.abstraction.ValidationField;

public class ValidationRegexField implements ValidationField {

    private final String regex;
    private final String[] targets;

    public ValidationRegexField(String regex,
                                String[] targets) {
        this.regex = regex;
        this.targets = targets;
    }

    public String getRegex() {
        return regex;
    }

    public String[] getTargets() {
        return targets;
    }

    @Override
    public Object[] getField() {
        return new Object[]{
                targets, regex
        };
    }
}
