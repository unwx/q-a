package qa.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import qa.dto.request.answer.*;
import qa.dto.validation.wrapper.answer.*;
import qa.exceptions.validator.ValidationException;
import qa.logger.TestLogger;
import qa.source.ValidationPropertyDataSource;
import qa.tools.annotations.MockitoTest;
import qa.validator.ValidationChainAdditionalImpl;
import qa.validator.abstraction.ValidationChainAdditional;
import util.dao.query.params.AnswerQueryParameters;
import util.validation.ValidationTestUtil;

@MockitoTest
public class ValidationAnswerRequestsTest {

    private ValidationChainAdditional validationChain;
    private ValidationPropertyDataSource propertyDataSource;

    private final TestLogger logger = new TestLogger(ValidationAnswerRequestsTest.class);

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
    class create {

        @Test
        void valid() {
            logger.trace(LOG_VALID);
            final AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                    new AnswerCreateRequest(5L, AnswerQueryParameters.TEXT), propertyDataSource
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace(LOG_INVALID_ID);
            final AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                    new AnswerCreateRequest(-5L, AnswerQueryParameters.TEXT), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_text() {
            logger.trace(LOG_INVALID_TEXT);
            final AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                    new AnswerCreateRequest(5L, "idk"), propertyDataSource
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class edit {

        @Test
        void valid() {
            logger.trace(LOG_VALID);
            final AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(
                    new AnswerEditRequest(1L, AnswerQueryParameters.TEXT),
                    propertyDataSource
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace(LOG_INVALID_ID);
            final AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(
                    new AnswerEditRequest(-5L, AnswerQueryParameters.TEXT), propertyDataSource
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_text() {
            logger.trace(LOG_INVALID_TEXT);
            final AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(
                    new AnswerEditRequest(1L, "ahah lol! disvote."), propertyDataSource
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class answered {

        @Test
        void valid() {
            logger.trace(LOG_VALID);
            final AnswerAnsweredRequestValidationWrapper validationWrapper = new AnswerAnsweredRequestValidationWrapper(
                    new AnswerAnsweredRequest(1L)
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace(LOG_INVALID_ID);
            final AnswerAnsweredRequestValidationWrapper validationWrapper = new AnswerAnsweredRequestValidationWrapper(
                    new AnswerAnsweredRequest(-5L)
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class delete {

        @Test
        void valid() {
            logger.trace(LOG_VALID);
            final AnswerDeleteRequestValidationWrapper validationWrapper = new AnswerDeleteRequestValidationWrapper(
                    new AnswerDeleteRequest(1L)
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace(LOG_INVALID_ID);
            final AnswerDeleteRequestValidationWrapper validationWrapper = new AnswerDeleteRequestValidationWrapper(
                    new AnswerDeleteRequest(-5L)
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class get {
        @Test
        void valid() {
            logger.trace(LOG_VALID);
            final AnswerGetFullRequestValidationWrapper validationWrapper = new AnswerGetFullRequestValidationWrapper(
                    new AnswerGetFullRequest(1L, 1)
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace(LOG_INVALID_ID);
            final AnswerGetFullRequestValidationWrapper validationWrapper = new AnswerGetFullRequestValidationWrapper(
                    new AnswerGetFullRequest(-1L, 1)
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_page() {
            logger.trace(LOG_INVALID_PAGE);
            final AnswerGetFullRequestValidationWrapper validationWrapper = new AnswerGetFullRequestValidationWrapper(
                    new AnswerGetFullRequest(1L, 0)
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class like {
        @Test
        void valid() {
            logger.trace(LOG_VALID);
            final AnswerLikeRequestValidationWrapper validationWrapper = new AnswerLikeRequestValidationWrapper(
                    new AnswerLikeRequest(1L)
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace(LOG_INVALID_ID);
            final AnswerLikeRequestValidationWrapper validationWrapper = new AnswerLikeRequestValidationWrapper(
                    new AnswerLikeRequest(-1L)
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }
}
