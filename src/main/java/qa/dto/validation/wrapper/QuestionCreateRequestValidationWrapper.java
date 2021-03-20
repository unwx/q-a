package qa.dto.validation.wrapper;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.QuestionCreateRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validators.abstraction.ValidationEntity;
import qa.validators.additional.TagsValidator;
import qa.validators.entities.*;

public class QuestionCreateRequestValidationWrapper extends QuestionCreateRequest implements ValidationEntity {

    private final ValidationPropertyDataSource propertiesDataSource;

    public QuestionCreateRequestValidationWrapper(QuestionCreateRequest request,
                                                  ValidationPropertyDataSource propertiesDataSource) {
        super(request.getTitle(), request.getText(), request.getTags());
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
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ValidationAdditional[] getAdditional() {
        return new ValidationAdditional[]{
                new ValidationAdditional(getTags(), new TagsValidator(propertiesDataSource))
        };
    }

    @Override
    @Nullable
    public ValidationRegexField[] getRegexFields() {
        return new ValidationRegexField[]{
                new ValidationRegexField(propertiesDataSource.getQUESTION_TITLE_REGEXP(), new String[]{getTitle()})
        };
    }
}
