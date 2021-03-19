package qa.dto.validation.wrapper;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.QuestionCreateRequest;
import qa.source.PropertiesDataSource;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.additional.TagsValidator;
import qa.validators.entities.ValidationAdditional;
import qa.validators.entities.ValidationNumberField;
import qa.validators.entities.ValidationObjectField;
import qa.validators.entities.ValidationStringField;

public class QuestionCreateRequestValidationWrapper extends QuestionCreateRequest implements ValidationEntity {

    private final PropertiesDataSource propertiesDataSource;

    public QuestionCreateRequestValidationWrapper(QuestionCreateRequest request,
                                                  PropertiesDataSource propertiesDataSource) {
        super(request.getTitle(), request.getTitle(), request.getTags());
        this.propertiesDataSource = propertiesDataSource;
    }

    @Override
    @Nullable
    public ValidationStringField[] getStringFields() {
        return new ValidationStringField[]{
                new ValidationStringField(
                        getTitle(),
                        propertiesDataSource.getQUESTION_TITLE_LENGTH_MIN(),
                        propertiesDataSource.getQUESTION_TITLE_LENGTH_MAX()),
                new ValidationStringField(
                        getText(),
                        propertiesDataSource.getQUESTION_TEXT_LENGTH_MIN(),
                        propertiesDataSource.getQUESTION_TEXT_LENGTH_MAX())
        };
    }

    @Override
    @Nullable
    public ValidationNumberField[] getNumberFields() {
        return new ValidationNumberField[0];
    }

    @Override
    @Nullable
    public ValidationObjectField[] getObjectFields() {
        return new ValidationObjectField[0];
    }

    @Override
    @Nullable
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ValidationAdditional[] getAdditional() {
        return new ValidationAdditional[]{
                new ValidationAdditional(getTags(), new TagsValidator(propertiesDataSource))
        };
    }
}
