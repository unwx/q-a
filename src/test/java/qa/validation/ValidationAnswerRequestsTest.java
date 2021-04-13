package qa.validation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import qa.dto.request.answer.AnswerAnsweredRequest;
import qa.dto.request.answer.AnswerCreateRequest;
import qa.dto.request.answer.AnswerDeleteRequest;
import qa.dto.request.answer.AnswerEditRequest;
import qa.dto.validation.wrapper.answer.AnswerAnsweredRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerDeleteRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerEditRequestValidationWrapper;
import qa.exceptions.validator.ValidationException;
import qa.logger.TestLogger;
import qa.source.ValidationPropertyDataSource;
import qa.tools.annotations.Logged;
import qa.tools.annotations.MockitoTest;
import qa.util.dao.query.params.AnswerQueryParameters;
import qa.util.validation.ValidationTestUtil;
import qa.validators.ValidationChainAdditionalImpl;
import qa.validators.abstraction.ValidationChainAdditional;

@MockitoTest
public class ValidationAnswerRequestsTest {

    private final ValidationChainAdditional validationChain = new ValidationChainAdditionalImpl();
    private ValidationPropertyDataSource propertyDataSource;

    private final TestLogger logger = new TestLogger(ValidationAnswerRequestsTest.class);

    @BeforeAll
    void init() {
        propertyDataSource = ValidationTestUtil.mockValidationProperties();
    }

    @Logged
    class create {

        @BeforeAll
        void init() {
            logger.nested(create.class);
        }

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

    @Logged
    class edit {

        @BeforeAll
        void init() {
            logger.nested(edit.class);
        }

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

    @Logged
    class answered {

        @BeforeAll
        void init() {
            logger.nested(answered.class);
        }

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

    @Logged
    class delete {

        @BeforeAll
        void init() {
            logger.nested(delete.class);
        }

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

    @AfterAll
    void close() {
        logger.end();
    }
}
