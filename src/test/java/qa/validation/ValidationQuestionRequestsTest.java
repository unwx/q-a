package qa.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import qa.dto.request.question.*;
import qa.dto.validation.wrapper.question.*;
import qa.exceptions.validator.ValidationException;
import qa.logger.TestLogger;
import qa.source.ValidationPropertyDataSource;
import qa.tools.annotations.MockitoTest;
import qa.util.dao.query.params.QuestionQueryParameters;
import qa.util.validation.ValidationTestUtil;
import qa.validators.ValidationChainAdditionalImpl;
import qa.validators.abstraction.ValidationChainAdditional;

@MockitoTest
public class ValidationQuestionRequestsTest {

    private final ValidationChainAdditional validationChain = new ValidationChainAdditionalImpl();
    private ValidationPropertyDataSource propertyDataSource;

    private final TestLogger logger = new TestLogger(ValidationQuestionRequestsTest.class);

    @BeforeAll
    void init() {
        propertyDataSource = ValidationTestUtil.mockValidationProperties();
    }

    @Nested
    class create {

        @Test
        void valid() {
            logger.trace("valid");
            QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                    new QuestionCreateRequest(
                            QuestionQueryParameters.TITLE,
                            QuestionQueryParameters.TEXT,
                            QuestionQueryParameters.TAGS_ARRAY), propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_title() {
            logger.trace("invalid title");
            QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                    new QuestionCreateRequest(
                            "How do",
                            QuestionQueryParameters.TEXT,
                            QuestionQueryParameters.TAGS_ARRAY), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_text() {
            logger.trace("invalid text");
            QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                    new QuestionCreateRequest(
                            QuestionQueryParameters.TITLE,
                            "how do",
                            QuestionQueryParameters.TAGS_ARRAY), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Nested
        class invalid_tags {

            @Test
            void null_tag() {
                logger.trace("null tag");
                QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                        new QuestionCreateRequest(
                                QuestionQueryParameters.TITLE,
                                QuestionQueryParameters.TEXT,
                                new String[]{null, "mockito", "autowired"}), propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void by_regex_pattern() {
                logger.trace("invalid by regex pattern");
                QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                        new QuestionCreateRequest(
                                QuestionQueryParameters.TITLE,
                                QuestionQueryParameters.TEXT,
                                new String[]{"sp_!#@$", "q"}), propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }
    }

    @Nested
    class edit {

        @Test
        void valid() {
            logger.trace("valid");
            QuestionEditRequestValidationWrapper validationWrapper = new QuestionEditRequestValidationWrapper(
                    new QuestionEditRequest(
                            1L,
                            QuestionQueryParameters.TEXT,
                            QuestionQueryParameters.TAGS_ARRAY),
                    propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace("invalid id");
            QuestionEditRequestValidationWrapper validationWrapper = new QuestionEditRequestValidationWrapper(
                    new QuestionEditRequest(
                            -5L,
                            QuestionQueryParameters.TEXT,
                            QuestionQueryParameters.TAGS_ARRAY),
                    propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class delete {

        @Test
        void valid() {
            logger.trace("valid");
            QuestionDeleteRequestValidationWrapper validationWrapper = new QuestionDeleteRequestValidationWrapper(
                    new QuestionDeleteRequest(5L));
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace("invalid id");
            QuestionDeleteRequestValidationWrapper validationWrapper = new QuestionDeleteRequestValidationWrapper(
                    new QuestionDeleteRequest(-5L));
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class get {

        @Nested
        class question_views {

            @Test
            void valid() {
                logger.trace("valid");
                QuestionGetViewsRequestValidationWrapper validationWrapper = new QuestionGetViewsRequestValidationWrapper(
                        new QuestionGetViewsRequest(1));
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Nested
            class invalid_page {

                @Test
                void zero() {
                    logger.trace("zero page");
                    QuestionGetViewsRequestValidationWrapper validationWrapper = new QuestionGetViewsRequestValidationWrapper(
                            new QuestionGetViewsRequest(0));
                    Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }

                @Test
                void negative() {
                    logger.trace("-page");
                    QuestionGetViewsRequestValidationWrapper validationWrapper = new QuestionGetViewsRequestValidationWrapper(
                            new QuestionGetViewsRequest(-3));
                    Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }
            }
        }

        @Nested
        class full_question {
            @Test
            void valid() {
                logger.trace("valid");
                QuestionGetFullRequestValidationWrapper validationWrapper = new QuestionGetFullRequestValidationWrapper(
                        new QuestionGetFullRequest(1L));
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace("invalid id");
                QuestionGetFullRequestValidationWrapper validationWrapper = new QuestionGetFullRequestValidationWrapper(
                        new QuestionGetFullRequest(-5L));
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class question_comments {

            @Test
            void valid() {
                logger.trace("valid");
                QuestionGetCommentsRequestValidationWrapper validationWrapper = new QuestionGetCommentsRequestValidationWrapper(
                        new QuestionGetCommentsRequest(1L, 1));
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Nested
            class invalid {

                @Test
                void page() {
                    logger.trace("invalid page");
                    QuestionGetCommentsRequestValidationWrapper validationWrapper = new QuestionGetCommentsRequestValidationWrapper(
                            new QuestionGetCommentsRequest(1L, 0));
                    Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }

                @Test
                void question_id() {
                    logger.trace("invalid question id");
                    QuestionGetCommentsRequestValidationWrapper validationWrapper = new QuestionGetCommentsRequestValidationWrapper(
                            new QuestionGetCommentsRequest(-5L, 1));
                    Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }
            }
        }
    }
}
