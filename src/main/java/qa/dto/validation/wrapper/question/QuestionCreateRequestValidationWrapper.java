package qa.dto.validation.wrapper.question;

import org.jetbrains.annotations.Nullable;
import qa.dto.request.question.QuestionCreateRequest;
import qa.source.ValidationPropertyDataSource;
import qa.validator.abstraction.ValidationWrapper;
import qa.validator.additional.TagsValidator;
import qa.validator.entities.ValidationAdditional;
import qa.validator.entities.ValidationStringField;

public class QuestionCreateRequestValidationWrapper extends QuestionCreateRequest implements ValidationWrapper {

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
                        propertiesDataSource.getQuestion().getQUESTION_TITLE_LENGTH_MIN(),
                        propertiesDataSource.getQuestion().getQUESTION_TITLE_LENGTH_MAX()),
                new ValidationStringField(
                        getText(),
                        propertiesDataSource.getQuestion().getQUESTION_TEXT_LENGTH_MIN(),
                        propertiesDataSource.getQuestion().getQUESTION_TEXT_LENGTH_MAX())
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
}
