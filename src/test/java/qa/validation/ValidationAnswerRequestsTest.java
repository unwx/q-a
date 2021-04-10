package qa.validation;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.dto.request.answer.AnswerAnsweredRequest;
import qa.dto.request.answer.AnswerCreateRequest;
import qa.dto.request.answer.AnswerDeleteRequest;
import qa.dto.request.answer.AnswerEditRequest;
import qa.dto.validation.wrapper.answer.AnswerAnsweredRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerDeleteRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerEditRequestValidationWrapper;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.util.dao.query.params.AnswerQueryParameters;
import qa.util.validation.ValidationTestUtil;
import qa.validators.ValidationChainAdditionalImpl;
import qa.validators.abstraction.ValidationChainAdditional;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ValidationAnswerRequestsTest {

    private final ValidationChainAdditional validationChain = new ValidationChainAdditionalImpl();
    private ValidationPropertyDataSource propertyDataSource;

    @BeforeAll
    void init() {
        propertyDataSource = ValidationTestUtil.mockValidationProperties();
    }

    @Nested
    class create {
        @Test
        void valid() {
            AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                    new AnswerCreateRequest(
                            5L, AnswerQueryParameters.TEXT), propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                    new AnswerCreateRequest(-5L, AnswerQueryParameters.TEXT), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_text() {
            AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                    new AnswerCreateRequest(5L, "idk"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class edit {
        @Test
        void valid() {
            AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(
                    new AnswerEditRequest(1L, AnswerQueryParameters.TEXT),
                    propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(
                    new AnswerEditRequest(-5L, AnswerQueryParameters.TEXT), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_text() {
            AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(
                    new AnswerEditRequest(1L, "ahah lol! disvote."), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class answered {
        @Test
        void valid() {
            AnswerAnsweredRequestValidationWrapper validationWrapper = new AnswerAnsweredRequestValidationWrapper(
                    new AnswerAnsweredRequest(1L));
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            AnswerAnsweredRequestValidationWrapper validationWrapper = new AnswerAnsweredRequestValidationWrapper(
                    new AnswerAnsweredRequest(-5L));
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class delete {
        @Test
        void valid() {
            AnswerDeleteRequestValidationWrapper validationWrapper = new AnswerDeleteRequestValidationWrapper(
                    new AnswerDeleteRequest(1L));
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            AnswerDeleteRequestValidationWrapper validationWrapper = new AnswerDeleteRequestValidationWrapper(
                    new AnswerDeleteRequest(-5L));
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }
}
