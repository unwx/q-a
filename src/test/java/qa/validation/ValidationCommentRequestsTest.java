package qa.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
public class ValidationCommentRequestsTest {

    private ValidationChainAdditional validationChain;
    private ValidationPropertyDataSource propertyDataSource;

    private final TestLogger logger = new TestLogger(ValidationCommentRequestsTest.class);

    private static final String LOG_VALID               = "valid";
    private static final String LOG_INVALID_ID          = "invalid id";
    private static final String LOG_INVALID_TEXT        = "invalid text";
    private static final String LOG_INVALID_PAGE        = "invalid page";

    @BeforeAll
    void init() {
        validationChain = Mockito.spy(ValidationChainAdditionalImpl.class);
        propertyDataSource = ValidationTestUtil.mockValidationProperties();
    }

    @Nested
    class question {

        @Nested
        class create {

            @Test
            void valid() {
                logger.trace(LOG_VALID);
                final CommentQuestionCreateRequestValidationWrapper validationWrapper = new CommentQuestionCreateRequestValidationWrapper(
                        new CommentQuestionCreateRequest(1L, CommentQueryParameters.TEXT),
                        propertyDataSource
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace(LOG_INVALID_ID);
                final CommentQuestionCreateRequestValidationWrapper validationWrapper = new CommentQuestionCreateRequestValidationWrapper(
                        new CommentQuestionCreateRequest(-5L, CommentQueryParameters.TEXT),
                        propertyDataSource
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                logger.trace(LOG_INVALID_TEXT);
                final CommentQuestionCreateRequestValidationWrapper validationWrapper = new CommentQuestionCreateRequestValidationWrapper(
                        new CommentQuestionCreateRequest(1L, "? wut"),
                        propertyDataSource
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class edit {

            @Test
            void valid() {
                logger.trace(LOG_VALID);
                final CommentQuestionEditRequestValidationWrapper validationWrapper = new CommentQuestionEditRequestValidationWrapper(
                        new CommentQuestionEditRequest(1L, CommentQueryParameters.TEXT),
                        propertyDataSource
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace(LOG_INVALID_ID);
                final CommentQuestionEditRequestValidationWrapper validationWrapper = new CommentQuestionEditRequestValidationWrapper(
                        new CommentQuestionEditRequest(-5L, CommentQueryParameters.TEXT),
                        propertyDataSource
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                logger.trace(LOG_INVALID_TEXT);
                final CommentQuestionEditRequestValidationWrapper validationWrapper = new CommentQuestionEditRequestValidationWrapper(
                        new CommentQuestionEditRequest(-5L, "? wut"),
                        propertyDataSource
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class delete {

            @Test
            void valid() {
                logger.trace(LOG_VALID);
                final CommentQuestionDeleteRequestValidationWrapper validationWrapper = new CommentQuestionDeleteRequestValidationWrapper(
                        new CommentQuestionDeleteRequest(1L)
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace(LOG_INVALID_ID);
                final CommentQuestionDeleteRequestValidationWrapper validationWrapper = new CommentQuestionDeleteRequestValidationWrapper(
                        new CommentQuestionDeleteRequest(-5L)
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class get {

            @Test
            void valid() {
                logger.trace(LOG_VALID);
                final CommentQuestionGetRequestValidationWrapper validationWrapper = new CommentQuestionGetRequestValidationWrapper(
                        new CommentQuestionGetRequest(1L, 1)
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace(LOG_INVALID_ID);
                final CommentQuestionGetRequestValidationWrapper validationWrapper = new CommentQuestionGetRequestValidationWrapper(
                        new CommentQuestionGetRequest(-1L, 1)
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_page() {
                logger.trace(LOG_INVALID_PAGE);
                final CommentQuestionGetRequestValidationWrapper validationWrapper = new CommentQuestionGetRequestValidationWrapper(
                        new CommentQuestionGetRequest(1L, 0)
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class like {
            @Test
            void valid() {
                logger.trace(LOG_VALID);
                final CommentQuestionLikeRequestValidationWrapper validationWrapper = new CommentQuestionLikeRequestValidationWrapper(
                        new CommentQuestionLikeRequest(1L)
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace(LOG_INVALID_ID);
                final CommentQuestionLikeRequestValidationWrapper validationWrapper = new CommentQuestionLikeRequestValidationWrapper(
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
                logger.trace(LOG_VALID);
                final CommentAnswerCreateRequestValidationWrapper validationWrapper = new CommentAnswerCreateRequestValidationWrapper(
                        new CommentAnswerCreateRequest(1L, CommentQueryParameters.TEXT),
                        propertyDataSource
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace(LOG_INVALID_ID);
                final CommentAnswerCreateRequestValidationWrapper validationWrapper = new CommentAnswerCreateRequestValidationWrapper(
                        new CommentAnswerCreateRequest(-5L, CommentQueryParameters.TEXT),
                        propertyDataSource
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                logger.trace(LOG_INVALID_TEXT);
                final CommentAnswerCreateRequestValidationWrapper validationWrapper = new CommentAnswerCreateRequestValidationWrapper(
                        new CommentAnswerCreateRequest(1L, "? wut"),
                        propertyDataSource
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class edit {

            @Test
            void valid() {
                logger.trace(LOG_VALID);
                final CommentAnswerEditRequestValidationWrapper validationWrapper = new CommentAnswerEditRequestValidationWrapper(
                        new CommentAnswerEditRequest(1L, CommentQueryParameters.TEXT),
                        propertyDataSource
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace(LOG_INVALID_ID);
                final CommentAnswerEditRequestValidationWrapper validationWrapper = new CommentAnswerEditRequestValidationWrapper(
                        new CommentAnswerEditRequest(-5L, "thank you! @username. :)"),
                        propertyDataSource
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                logger.trace(LOG_INVALID_TEXT);
                final CommentAnswerEditRequestValidationWrapper validationWrapper = new CommentAnswerEditRequestValidationWrapper(
                        new CommentAnswerEditRequest(-5L, "? wut"),
                        propertyDataSource
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class delete {

            @Test
            void valid() {
                logger.trace(LOG_VALID);
                final CommentAnswerDeleteRequestValidationWrapper validationWrapper = new CommentAnswerDeleteRequestValidationWrapper(
                        new CommentAnswerDeleteRequest(1L)
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace(LOG_INVALID_ID);
                final CommentAnswerDeleteRequestValidationWrapper validationWrapper = new CommentAnswerDeleteRequestValidationWrapper(
                        new CommentAnswerDeleteRequest(-5L)
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class get {

            @Test
            void valid() {
                logger.trace(LOG_VALID);
                final CommentAnswerGetRequestValidationWrapper validationWrapper = new CommentAnswerGetRequestValidationWrapper(
                        new CommentAnswerGetRequest(1L, 1)
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace(LOG_INVALID_ID);
                final CommentAnswerGetRequestValidationWrapper validationWrapper = new CommentAnswerGetRequestValidationWrapper(
                        new CommentAnswerGetRequest(-1L, 1)
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_page() {
                logger.trace(LOG_INVALID_PAGE);
                final CommentAnswerGetRequestValidationWrapper validationWrapper = new CommentAnswerGetRequestValidationWrapper(
                        new CommentAnswerGetRequest(1L, 0)
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class like {
            @Test
            void valid() {
                logger.trace(LOG_VALID);
                final CommentAnswerLikeRequestValidationWrapper validationWrapper = new CommentAnswerLikeRequestValidationWrapper(
                        new CommentAnswerLikeRequest(1L)
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace(LOG_INVALID_ID);
                final CommentAnswerLikeRequestValidationWrapper validationWrapper = new CommentAnswerLikeRequestValidationWrapper(
                        new CommentAnswerLikeRequest(-1L)
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }
    }
}
