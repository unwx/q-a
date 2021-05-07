package qa.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import qa.dto.request.comment.CommentQuestionGetRequest;
import qa.dto.request.comment.CommentQuestionLikeRequest;
import qa.dto.request.question.*;
import qa.dto.validation.wrapper.comment.CommentQuestionLikeRequestValidationWrapper;
import qa.dto.validation.wrapper.question.*;
import qa.exceptions.validator.ValidationException;
import qa.logger.TestLogger;
import qa.source.ValidationPropertyDataSource;
import qa.tools.annotations.MockitoTest;
import qa.validator.ValidationChainAdditionalImpl;
import qa.validator.abstraction.ValidationChainAdditional;
import util.dao.query.params.QuestionQueryParameters;
import util.validation.ValidationTestUtil;

@MockitoTest
public class ValidationQuestionRequestsTest {

    private ValidationChainAdditional validationChain;
    private ValidationPropertyDataSource propertyDataSource;

    private final TestLogger logger = new TestLogger(ValidationQuestionRequestsTest.class);

    private static final String LOG_VALID                   = "valid";
    private static final String LOG_INVALID_ID              = "invalid id";
    private static final String LOG_INVALID_TITLE           = "invalid title";
    private static final String LOG_INVALID_TEXT            = "invalid text";
    private static final String LOG_INVALID_PAGE            = "invalid page";
    private static final String LOG_INVALID_TAG             = "invalid tag";
    private static final String LOG_INVALID_TAG_REGEX       = "invalid by regex pattern";

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
            final QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                    new QuestionCreateRequest(
                            QuestionQueryParameters.TITLE,
                            QuestionQueryParameters.TEXT,
                            QuestionQueryParameters.TAGS_ARRAY
                    ),
                    propertyDataSource
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_title() {
            logger.trace(LOG_INVALID_TITLE);
            final QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                    new QuestionCreateRequest(
                            "How do",
                            QuestionQueryParameters.TEXT,
                            QuestionQueryParameters.TAGS_ARRAY
                    ),
                    propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_text() {
            logger.trace(LOG_INVALID_TEXT);
            final QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                    new QuestionCreateRequest(
                            QuestionQueryParameters.TITLE,
                            "how do",
                            QuestionQueryParameters.TAGS_ARRAY
                    ),
                    propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Nested
        class invalid_tags {

            @Test
            void null_tag() {
                logger.trace(LOG_INVALID_TAG);
                final QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                        new QuestionCreateRequest(
                                QuestionQueryParameters.TITLE,
                                QuestionQueryParameters.TEXT,
                                new String[]{null, "mockito", "autowired"}
                        ),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void by_regex_pattern() {
                logger.trace(LOG_INVALID_TAG_REGEX);
                final QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                        new QuestionCreateRequest(
                                QuestionQueryParameters.TITLE,
                                QuestionQueryParameters.TEXT,
                                new String[]{"sp_!#@$", "q"}
                        ),
                        propertyDataSource
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }
    }

    @Nested
    class edit {

        @Test
        void valid() {
            logger.trace(LOG_VALID);
            final QuestionEditRequestValidationWrapper validationWrapper = new QuestionEditRequestValidationWrapper(
                    new QuestionEditRequest(
                            1L,
                            QuestionQueryParameters.TEXT,
                            QuestionQueryParameters.TAGS_ARRAY
                    ),
                    propertyDataSource
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace(LOG_INVALID_ID);
            final QuestionEditRequestValidationWrapper validationWrapper = new QuestionEditRequestValidationWrapper(
                    new QuestionEditRequest(
                            -5L,
                            QuestionQueryParameters.TEXT,
                            QuestionQueryParameters.TAGS_ARRAY
                    ),
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
            final QuestionDeleteRequestValidationWrapper validationWrapper = new QuestionDeleteRequestValidationWrapper(
                    new QuestionDeleteRequest(5L)
            );
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_id() {
            logger.trace(LOG_INVALID_ID);
            final QuestionDeleteRequestValidationWrapper validationWrapper = new QuestionDeleteRequestValidationWrapper(
                    new QuestionDeleteRequest(-5L)
            );
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class get {

        @Nested
        class question_views {

            @Test
            void valid() {
                logger.trace(LOG_VALID);
                final QuestionGetViewsRequestValidationWrapper validationWrapper = new QuestionGetViewsRequestValidationWrapper(
                        new QuestionGetViewsRequest(1)
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Nested
            class invalid_page {

                @Test
                void zero() {
                    logger.trace(LOG_INVALID_PAGE);
                    final QuestionGetViewsRequestValidationWrapper validationWrapper = new QuestionGetViewsRequestValidationWrapper(
                            new QuestionGetViewsRequest(0)
                    );
                    Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }

                @Test
                void negative() {
                    logger.trace(LOG_INVALID_PAGE);
                    final QuestionGetViewsRequestValidationWrapper validationWrapper = new QuestionGetViewsRequestValidationWrapper(
                            new QuestionGetViewsRequest(-3)
                    );
                    Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }
            }
        }

        @Nested
        class full_question {
            @Test
            void valid() {
                logger.trace(LOG_VALID);
                final QuestionGetFullRequestValidationWrapper validationWrapper = new QuestionGetFullRequestValidationWrapper(
                        new QuestionGetFullRequest(1L)
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                logger.trace(LOG_INVALID_ID);
                final QuestionGetFullRequestValidationWrapper validationWrapper = new QuestionGetFullRequestValidationWrapper(
                        new QuestionGetFullRequest(-5L)
                );
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class question_comments {

            @Test
            void valid() {
                logger.trace(LOG_VALID);
                final CommentQuestionGetRequestValidationWrapper validationWrapper = new CommentQuestionGetRequestValidationWrapper(
                        new CommentQuestionGetRequest(1L, 1)
                );
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Nested
            class invalid {

                @Test
                void page() {
                    logger.trace(LOG_INVALID_PAGE);
                    final CommentQuestionGetRequestValidationWrapper validationWrapper = new CommentQuestionGetRequestValidationWrapper(
                            new CommentQuestionGetRequest(1L, 0)
                    );
                    Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }

                @Test
                void question_id() {
                    logger.trace(LOG_INVALID_ID);
                    final CommentQuestionGetRequestValidationWrapper validationWrapper = new CommentQuestionGetRequestValidationWrapper(
                            new CommentQuestionGetRequest(-5L, 1)
                    );
                    Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }
            }
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
