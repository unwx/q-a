package qa.validator.chain;

import qa.validator.abstraction.ValidationWrapper;
import qa.validator.entities.ValidationIgnoreType;

import java.util.HashSet;

public class IgnoreFieldExtractor {

    public HashSet<ValidationIgnoreType> getIgnore(ValidationWrapper entity) {
        final HashSet<ValidationIgnoreType> ignore = new HashSet<>();
        this.regexPart(ignore, entity);
        this.stringFieldsPart(ignore, entity);
        this.numberFieldsPart(ignore, entity);
        this.objectFieldsPart(ignore, entity);
        return ignore;
    }

    private void regexPart(HashSet<ValidationIgnoreType> ignore, ValidationWrapper entity) {
        if (entity.getRegexFields() == null)
            ignore.add(ValidationIgnoreType.REGEX);
    }

    private void stringFieldsPart(HashSet<ValidationIgnoreType> ignore, ValidationWrapper entity) {
        if (entity.getStringFields() == null)
            ignore.add(ValidationIgnoreType.STRING);
    }

    private void numberFieldsPart(HashSet<ValidationIgnoreType> ignore, ValidationWrapper entity) {
        if (entity.getNumberFields() == null)
            ignore.add(ValidationIgnoreType.NUMBER);
    }

    private void objectFieldsPart(HashSet<ValidationIgnoreType> ignore, ValidationWrapper entity) {
        if (entity.getObjectFields() == null)
            ignore.add(ValidationIgnoreType.OBJECT);
    }
}
