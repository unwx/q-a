package qa.validators.chain;

import qa.validators.abstraction.ValidationEntity;
import qa.validators.entities.ValidationIgnoreType;

import java.util.HashSet;

public class IgnorePartValidator {

    public HashSet<ValidationIgnoreType> getIgnore(ValidationEntity entity) {
        HashSet<ValidationIgnoreType> ignore = new HashSet<>();
        regexPart(ignore, entity);
        stringFieldsPart(ignore, entity);
        numberFieldsPart(ignore, entity);
        objectFieldsPart(ignore, entity);
        return ignore;
    }

    private void regexPart(HashSet<ValidationIgnoreType> ignore, ValidationEntity entity) {
        if (entity.getRegexFields() == null)
            ignore.add(ValidationIgnoreType.REGEX);
    }

    private void stringFieldsPart(HashSet<ValidationIgnoreType> ignore, ValidationEntity entity) {
        if (entity.getStringFields() == null)
            ignore.add(ValidationIgnoreType.STRING);
    }

    private void numberFieldsPart(HashSet<ValidationIgnoreType> ignore, ValidationEntity entity) {
        if (entity.getNumberFields() == null)
            ignore.add(ValidationIgnoreType.NUMBER);
    }

    private void objectFieldsPart(HashSet<ValidationIgnoreType> ignore, ValidationEntity entity) {
        if (entity.getObjectFields() == null)
            ignore.add(ValidationIgnoreType.OBJECT);
    }
}
