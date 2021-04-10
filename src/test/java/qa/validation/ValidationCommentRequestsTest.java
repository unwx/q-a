package qa.validation;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.dto.request.comment.*;
import qa.dto.validation.wrapper.comment.*;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.util.dao.query.params.CommentQueryParameters;
import qa.util.validation.ValidationTestUtil;
import qa.validators.ValidationChainAdditionalImpl;
import qa.validators.abstraction.ValidationChainAdditional;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ValidationCommentRequestsTest {

    private final ValidationChainAdditional validationChain = new ValidationChainAdditionalImpl();
    private ValidationPropertyDataSource propertyDataSource;

    @BeforeAll
    void init() {
        propertyDataSource = ValidationTestUtil.mockValidationProperties();
    }

    @Nested
    class question {
        @Nested
        class create {
            @Test
            void valid() {
                CommentQuestionCreateRequestValidationWrapper validationWrapper = new CommentQuestionCreateRequestValidationWrapper(
                        new CommentQuestionCreateRequest(1L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                CommentQuestionCreateRequestValidationWrapper validationWrapper = new CommentQuestionCreateRequestValidationWrapper(
                        new CommentQuestionCreateRequest(-5L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                CommentQuestionCreateRequestValidationWrapper validationWrapper = new CommentQuestionCreateRequestValidationWrapper(
                        new CommentQuestionCreateRequest(1L, "? wut"),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class edit {
            @Test
            void valid() {
                CommentQuestionEditRequestValidationWrapper validationWrapper = new CommentQuestionEditRequestValidationWrapper(
                        new CommentQuestionEditRequest(1L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                CommentQuestionEditRequestValidationWrapper validationWrapper = new CommentQuestionEditRequestValidationWrapper(
                        new CommentQuestionEditRequest(-5L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                CommentQuestionEditRequestValidationWrapper validationWrapper = new CommentQuestionEditRequestValidationWrapper(
                        new CommentQuestionEditRequest(-5L, "? wut"),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class delete {
            @Test
            void valid() {
                CommentQuestionDeleteRequestValidationWrapper validationWrapper = new CommentQuestionDeleteRequestValidationWrapper(
                        new CommentQuestionDeleteRequest(1L));
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                CommentQuestionDeleteRequestValidationWrapper validationWrapper = new CommentQuestionDeleteRequestValidationWrapper(
                        new CommentQuestionDeleteRequest(-5L));
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }
    }

    @Nested
    class answer {
        @Nested
        class create {
            @Test
            void valid() {
                CommentAnswerCreateRequestValidationWrapper validationWrapper = new CommentAnswerCreateRequestValidationWrapper(
                        new CommentAnswerCreateRequest(1L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                CommentAnswerCreateRequestValidationWrapper validationWrapper = new CommentAnswerCreateRequestValidationWrapper(
                        new CommentAnswerCreateRequest(-5L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                CommentAnswerCreateRequestValidationWrapper validationWrapper = new CommentAnswerCreateRequestValidationWrapper(
                        new CommentAnswerCreateRequest(1L, "? wut"),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class edit {
            @Test
            void valid() {
                CommentAnswerEditRequestValidationWrapper validationWrapper = new CommentAnswerEditRequestValidationWrapper(
                        new CommentAnswerEditRequest(1L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                CommentAnswerEditRequestValidationWrapper validationWrapper = new CommentAnswerEditRequestValidationWrapper(
                        new CommentAnswerEditRequest(-5L, "thank you! @username. :)"),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                CommentAnswerEditRequestValidationWrapper validationWrapper = new CommentAnswerEditRequestValidationWrapper(
                        new CommentAnswerEditRequest(-5L, "? wut"),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class delete {
            @Test
            void valid() {
                CommentAnswerDeleteRequestValidationWrapper validationWrapper = new CommentAnswerDeleteRequestValidationWrapper(
                        new CommentAnswerDeleteRequest(1L));
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                CommentAnswerDeleteRequestValidationWrapper validationWrapper = new CommentAnswerDeleteRequestValidationWrapper(
                        new CommentAnswerDeleteRequest(-5L));
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }
    }
}
