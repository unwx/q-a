package qa.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import qa.dto.request.answer.*;
import qa.dto.validation.wrapper.answer.*;
import qa.exceptions.validator.ValidationException;
import qa.logger.TestLogger;
import qa.source.ValidationPropertyDataSource;
import qa.tools.annotations.MockitoTest;
import qa.util.dao.query.params.AnswerQueryParameters;
import qa.util.validation.ValidationTestUtil;
import qa.validator.ValidationChainAdditionalImpl;
import qa.validator.abstraction.ValidationChainAdditional;

@MockitoTest
public class ValidationAnswerRequestsTest {

    private final ValidationChainAdditional validationChain = new ValidationChainAdditionalImpl();
    private ValidationPropertyDataSource propertyDataSource;

    private final TestLogger logger = new TestLogger(ValidationAnswerRequestsTest.class);

    @BeforeAll
    void init() {
        propertyDataSource = ValidationTestUtil.mockValidationProperties();
    }

    @Nested
    class create {

        @Test
        void valid() {
            logger.trace("valid");
            AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                    new AnswerCreateRequest(
                            5L, AnswerQueryParameters.TEXT), propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace("invalid id");
            AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                    new AnswerCreateRequest(-5L, AnswerQueryParameters.TEXT), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_text() {
            logger.trace("invalid text");
            AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                    new AnswerCreateRequest(5L, "idk"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class edit {

        @Test
        void valid() {
            logger.trace("valid");
            AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(
                    new AnswerEditRequest(1L, AnswerQueryParameters.TEXT),
                    propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace("invalid id");
            AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(
                    new AnswerEditRequest(-5L, AnswerQueryParameters.TEXT), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_text() {
            logger.trace("invalid text");
            AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(
                    new AnswerEditRequest(1L, "ahah lol! disvote."), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class answered {

        @Test
        void valid() {
            logger.trace("valid");
            AnswerAnsweredRequestValidationWrapper validationWrapper = new AnswerAnsweredRequestValidationWrapper(
                    new AnswerAnsweredRequest(1L));
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace("invalid id");
            AnswerAnsweredRequestValidationWrapper validationWrapper = new AnswerAnsweredRequestValidationWrapper(
                    new AnswerAnsweredRequest(-5L));
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class delete {

        @Test
        void valid() {
            logger.trace("valid");
            AnswerDeleteRequestValidationWrapper validationWrapper = new AnswerDeleteRequestValidationWrapper(
                    new AnswerDeleteRequest(1L));
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace("invalid id");
            AnswerDeleteRequestValidationWrapper validationWrapper = new AnswerDeleteRequestValidationWrapper(
                    new AnswerDeleteRequest(-5L));
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class get {
        @Test
        void valid() {
            logger.trace("valid");
            AnswerGetFullRequestValidationWrapper validationWrapper = new AnswerGetFullRequestValidationWrapper(
                    new AnswerGetFullRequest(1L, 1)
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace("invalid id");
            AnswerGetFullRequestValidationWrapper validationWrapper = new AnswerGetFullRequestValidationWrapper(
                    new AnswerGetFullRequest(-1L, 1)
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_page() {
            logger.trace("invalid page");
            AnswerGetFullRequestValidationWrapper validationWrapper = new AnswerGetFullRequestValidationWrapper(
                    new AnswerGetFullRequest(1L, 0)
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class like {
        @Test
        void valid() {
            logger.trace("valid");
            AnswerLikeRequestValidationWrapper validationWrapper = new AnswerLikeRequestValidationWrapper(
                    new AnswerLikeRequest(1L)
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace("invalid id");
            AnswerLikeRequestValidationWrapper validationWrapper = new AnswerLikeRequestValidationWrapper(
                    new AnswerLikeRequest(-1L)
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }
}
