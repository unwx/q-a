package qa.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import qa.dto.request.comment.*;
import qa.dto.validation.wrapper.answer.CommentAnswerGetRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.*;
import qa.dto.validation.wrapper.question.CommentQuestionGetRequestValidationWrapper;
import qa.exceptions.validator.ValidationException;
import qa.logger.TestLogger;
import qa.source.ValidationPropertyDataSource;
import qa.tools.annotations.MockitoTest;
import qa.validator.ValidationChainAdditionalImpl;
import qa.validator.abstraction.ValidationChainAdditional;
import util.dao.query.params.CommentQueryParameters;
import util.validation.ValidationTestUtil;

@MockitoTest
public class ValidationCommentRequestsTest { // TODO REFACTOR

    private final ValidationChainAdditional validationChain = new ValidationChainAdditionalImpl();
    private ValidationPropertyDataSource propertyDataSource;

    private final TestLogger logger = new TestLogger(ValidationCommentRequestsTest.class);

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
                logger.trace("valid");
                CommentQuestionCreateRequestValidationWrapper validationWrapper = new CommentQuestionCreateRequestValidationWrapper(
                        new CommentQuestionCreateRequest(1L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace("invalid id");
                CommentQuestionCreateRequestValidationWrapper validationWrapper = new CommentQuestionCreateRequestValidationWrapper(
                        new CommentQuestionCreateRequest(-5L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                logger.trace("invalid text");
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
                logger.trace("valid");
                CommentQuestionEditRequestValidationWrapper validationWrapper = new CommentQuestionEditRequestValidationWrapper(
                        new CommentQuestionEditRequest(1L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace("invalid id");
                CommentQuestionEditRequestValidationWrapper validationWrapper = new CommentQuestionEditRequestValidationWrapper(
                        new CommentQuestionEditRequest(-5L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                logger.trace("invalid text");
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
                logger.trace("valid");
                CommentQuestionDeleteRequestValidationWrapper validationWrapper = new CommentQuestionDeleteRequestValidationWrapper(
                        new CommentQuestionDeleteRequest(1L));
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace("invalid id");
                CommentQuestionDeleteRequestValidationWrapper validationWrapper = new CommentQuestionDeleteRequestValidationWrapper(
                        new CommentQuestionDeleteRequest(-5L));
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class get {

            @Test
            void valid() {
                logger.trace("valid");
                CommentQuestionGetRequestValidationWrapper validationWrapper = new CommentQuestionGetRequestValidationWrapper(
                        new CommentQuestionGetRequest(1L, 1));
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace("invalid id");
                CommentQuestionGetRequestValidationWrapper validationWrapper = new CommentQuestionGetRequestValidationWrapper(
                        new CommentQuestionGetRequest(-1L, 1));
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_page() {
                logger.trace("invalid page");
                CommentQuestionGetRequestValidationWrapper validationWrapper = new CommentQuestionGetRequestValidationWrapper(
                        new CommentQuestionGetRequest(1L, 0));
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class like {
            @Test
            void valid() {
                logger.trace("valid");
                CommentQuestionLikeRequestValidationWrapper validationWrapper = new CommentQuestionLikeRequestValidationWrapper(
                        new CommentQuestionLikeRequest(1L)
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace("invalid id");
                CommentQuestionLikeRequestValidationWrapper validationWrapper = new CommentQuestionLikeRequestValidationWrapper(
                        new CommentQuestionLikeRequest(-1L)
                );
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
                logger.trace("valid");
                CommentAnswerCreateRequestValidationWrapper validationWrapper = new CommentAnswerCreateRequestValidationWrapper(
                        new CommentAnswerCreateRequest(1L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace("invalid id");
                CommentAnswerCreateRequestValidationWrapper validationWrapper = new CommentAnswerCreateRequestValidationWrapper(
                        new CommentAnswerCreateRequest(-5L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                logger.trace("invalid text");
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
                logger.trace("valid");
                CommentAnswerEditRequestValidationWrapper validationWrapper = new CommentAnswerEditRequestValidationWrapper(
                        new CommentAnswerEditRequest(1L, CommentQueryParameters.TEXT),
                        propertyDataSource);
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace("invalid id");
                CommentAnswerEditRequestValidationWrapper validationWrapper = new CommentAnswerEditRequestValidationWrapper(
                        new CommentAnswerEditRequest(-5L, "thank you! @username. :)"),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                logger.trace("invalid text");
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
                logger.trace("valid");
                CommentAnswerDeleteRequestValidationWrapper validationWrapper = new CommentAnswerDeleteRequestValidationWrapper(
                        new CommentAnswerDeleteRequest(1L));
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace("invalid id");
                CommentAnswerDeleteRequestValidationWrapper validationWrapper = new CommentAnswerDeleteRequestValidationWrapper(
                        new CommentAnswerDeleteRequest(-5L));
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class get {

            @Test
            void valid() {
                logger.trace("valid");
                CommentAnswerGetRequestValidationWrapper validationWrapper = new CommentAnswerGetRequestValidationWrapper(
                        new CommentAnswerGetRequest(1L, 1));
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace("invalid id");
                CommentAnswerGetRequestValidationWrapper validationWrapper = new CommentAnswerGetRequestValidationWrapper(
                        new CommentAnswerGetRequest(-1L, 1));
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_page() {
                logger.trace("invalid page");
                CommentAnswerGetRequestValidationWrapper validationWrapper = new CommentAnswerGetRequestValidationWrapper(
                        new CommentAnswerGetRequest(1L, 0));
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class like {
            @Test
            void valid() {
                logger.trace("valid");
                CommentAnswerLikeRequestValidationWrapper validationWrapper = new CommentAnswerLikeRequestValidationWrapper(
                        new CommentAnswerLikeRequest(1L)
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace("invalid id");
                CommentAnswerLikeRequestValidationWrapper validationWrapper = new CommentAnswerLikeRequestValidationWrapper(
                        new CommentAnswerLikeRequest(-1L)
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }
    }
}
