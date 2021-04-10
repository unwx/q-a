package qa.validation;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.dto.request.question.*;
import qa.dto.validation.wrapper.question.*;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.util.dao.query.params.QuestionQueryParameters;
import qa.util.validation.ValidationTestUtil;
import qa.validators.ValidationChainAdditionalImpl;
import qa.validators.abstraction.ValidationChainAdditional;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ValidationQuestionRequestsTest {

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
            QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                    new QuestionCreateRequest(
                            QuestionQueryParameters.TITLE,
                            QuestionQueryParameters.TEXT,
                            QuestionQueryParameters.TAGS_ARRAY), propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_title() {
            QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                    new QuestionCreateRequest(
                            "How do",
                            QuestionQueryParameters.TEXT,
                            QuestionQueryParameters.TAGS_ARRAY), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_text() {
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
                QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                        new QuestionCreateRequest(
                                QuestionQueryParameters.TITLE,
                                QuestionQueryParameters.TEXT,
                                new String[]{null, "mockito", "autowired"}), propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void by_regex_pattern() {
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
            QuestionDeleteRequestValidationWrapper validationWrapper = new QuestionDeleteRequestValidationWrapper(
                    new QuestionDeleteRequest(5L));
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
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
                QuestionGetViewsRequestValidationWrapper validationWrapper = new QuestionGetViewsRequestValidationWrapper(
                        new QuestionGetViewsRequest(1));
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Nested
            class invalid_page {
                @Test
                void zero() {
                    QuestionGetViewsRequestValidationWrapper validationWrapper = new QuestionGetViewsRequestValidationWrapper(
                            new QuestionGetViewsRequest(0));
                    Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }

                @Test
                void negative() {
                    QuestionGetViewsRequestValidationWrapper validationWrapper = new QuestionGetViewsRequestValidationWrapper(
                            new QuestionGetViewsRequest(-3));
                    Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }
            }
        }

        @Nested
        class full_question {
            @Test
            void valid () {
                QuestionGetFullRequestValidationWrapper validationWrapper = new QuestionGetFullRequestValidationWrapper(
                        new QuestionGetFullRequest(1L));
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                QuestionGetFullRequestValidationWrapper validationWrapper = new QuestionGetFullRequestValidationWrapper(
                        new QuestionGetFullRequest(-5L));
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }
    }
}
