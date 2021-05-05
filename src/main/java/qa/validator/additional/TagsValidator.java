package qa.validator.additional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;

import java.util.regex.Pattern;

@Component
public class TagsValidator extends AdditionalValidator<String[]> {

    private final int lengthMax;
    private final int lengthMin;
    private final int countMax;
    private final int countMin;

    private final Pattern pattern;

    private static final String ERR_TAGS_NULL       = "tags must not be null";
    private static final String ERR_TAG_LENGTH      = "tag length must be >= 2 and <= 20";
    private static final String ERR_TAG_CHARACTER   = "the tag has unresolved characters";
    private static final String ERR_TAG_COUNT       = "tags count must be >= 1 and <= 7";

    @Autowired
    public TagsValidator(ValidationPropertyDataSource propertiesDataSource) {
        this.lengthMax = propertiesDataSource.getQuestion().getQUESTION_TAG_LENGTH_MAX();
        this.lengthMin = propertiesDataSource.getQuestion().getQUESTION_TAG_LENGTH_MIN();
        this.countMax = propertiesDataSource.getQuestion().getQUESTION_TAGS_COUNT_MAX();
        this.countMin = propertiesDataSource.getQuestion().getQUESTION_TAGS_COUNT_MIN();
        this.pattern =  Pattern.compile(propertiesDataSource.getQuestion().getQUESTION_TAG_REGEXP());
    }

    @Override
    public void validate(String[] c) throws ValidationException {
        if (c == null)
            throw super.logAndThrow(ERR_TAGS_NULL);

        int counter = 0;
        for (String s : c) {
            if (s == null)
                throw super.logAndThrow(ERR_TAGS_NULL);

            if (s.length() > lengthMax || s.length() < lengthMin)
                throw super.logAndThrow(ERR_TAG_LENGTH);

            if (!pattern.matcher(s).find())
                throw super.logAndThrow(ERR_TAG_CHARACTER);

            counter++;
        }

        if (counter > countMax || counter < countMin)
            throw super.logAndThrow(ERR_TAG_COUNT);
    }
}
