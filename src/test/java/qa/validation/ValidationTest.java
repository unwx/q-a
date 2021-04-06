package qa.validation;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.dto.request.answer.AnswerAnsweredRequest;
import qa.dto.request.answer.AnswerCreateRequest;
import qa.dto.request.answer.AnswerDeleteRequest;
import qa.dto.request.answer.AnswerEditRequest;
import qa.dto.request.authentication.AuthenticationRequest;
import qa.dto.request.authentication.RegistrationRequest;
import qa.dto.request.comment.*;
import qa.dto.request.question.QuestionCreateRequest;
import qa.dto.request.question.QuestionDeleteRequest;
import qa.dto.request.question.QuestionEditRequest;
import qa.dto.validation.wrapper.answer.AnswerAnsweredRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerDeleteRequestValidationWrapper;
import qa.dto.validation.wrapper.answer.AnswerEditRequestValidationWrapper;
import qa.dto.validation.wrapper.authentication.AuthenticationRequestValidationWrapper;
import qa.dto.validation.wrapper.authentication.RegistrationRequestValidationWrapper;
import qa.dto.validation.wrapper.comment.*;
import qa.dto.validation.wrapper.question.QuestionCreateRequestValidationWrapper;
import qa.dto.validation.wrapper.question.QuestionDeleteRequestValidationWrapper;
import qa.dto.validation.wrapper.question.QuestionEditRequestValidationWrapper;
import qa.exceptions.validator.ValidationException;
import qa.source.ValidationPropertyDataSource;
import qa.validators.ValidationChainAdditionalImpl;
import qa.validators.abstraction.ValidationChainAdditional;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ValidationTest {

    private ValidationPropertyDataSource propertyDataSource;
    private final ValidationChainAdditional validationChain = new ValidationChainAdditionalImpl();

    @BeforeAll
    void init() {
        propertyDataSource = Mockito.mock(ValidationPropertyDataSource.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.lenient().when(propertyDataSource.getAnswer().getANSWER_TEXT_LENGTH_MAX()).thenReturn(2000);
        Mockito.lenient().when(propertyDataSource.getAnswer().getANSWER_TEXT_LENGTH_MIN()).thenReturn(20);
        Mockito.lenient().when(propertyDataSource.getAuthentication().getAUTHENTICATION_PASSWORD_LENGTH_MAX()).thenReturn(30);
        Mockito.lenient().when(propertyDataSource.getAuthentication().getAUTHENTICATION_PASSWORD_LENGTH_MIN()).thenReturn(10);
        Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TAG_LENGTH_MAX()).thenReturn(20);
        Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TAG_LENGTH_MIN()).thenReturn(2);
        Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TAG_REGEXP()).thenReturn("^(?![_.\\- ])(?!.*[_.-]{2})[a-zA-Z0-9._\\-]+(?<![_.\\- ])$");
        Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TAGS_COUNT_MAX()).thenReturn(7);
        Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TAGS_COUNT_MIN()).thenReturn(1);
        Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TEXT_LENGTH_MAX()).thenReturn(2000);
        Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TEXT_LENGTH_MIN()).thenReturn(50);
        Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TITLE_LENGTH_MAX()).thenReturn(50);
        Mockito.lenient().when(propertyDataSource.getQuestion().getQUESTION_TITLE_LENGTH_MIN()).thenReturn(10);
        Mockito.lenient().when(propertyDataSource.getUser().getUSER_ABOUT_LENGTH_MAX()).thenReturn(1024);
        Mockito.lenient().when(propertyDataSource.getUser().getUSER_ABOUT_LENGTH_MIN()).thenReturn(1);
        Mockito.lenient().when(propertyDataSource.getUser().getUSER_USERNAME_LENGTH_MAX()).thenReturn(30);
        Mockito.lenient().when(propertyDataSource.getUser().getUSER_USERNAME_LENGTH_MIN()).thenReturn(2);
        Mockito.lenient().when(propertyDataSource.getUser().getUSER_USERNAME_REGEXP()).thenReturn("^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
        Mockito.lenient().when(propertyDataSource.getComment().getCOMMENT_TEXT_LENGTH_MIN()).thenReturn(15);
        Mockito.lenient().when(propertyDataSource.getComment().getCOMMENT_TEXT_LENGTH_MAX()).thenReturn(500);
    }

    @Nested
    class registration {
        @Test
        void valid() {
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest("username132", "name@email.com", "password*(J:S#JOP$"), propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields() {
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest(null, "name@email.com", "password*(J:S#JOP$"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields_1() {
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest("username", null, "password*(J:S#JOP$"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields_2() {
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest("username", "email@email.com", null), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_email() {
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest("correct", "emai-@'''.com", "password*(J:S#JOP$"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_password() {
            RegistrationRequestValidationWrapper validationWrapper = new RegistrationRequestValidationWrapper(
                    new RegistrationRequest("correct", "email@email.com", "12345"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class login {
        @Test
        void valid() {
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest("email@email.com", "password12344321"), propertyDataSource);
            Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields() {
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest(null, "password12344321"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_null_fields_1() {
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest("email@email.com", null), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_email() {
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest("emai-@'''.com", "password12344321"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }

        @Test
        void invalid_password() {
            AuthenticationRequestValidationWrapper validationWrapper = new AuthenticationRequestValidationWrapper(
                    new AuthenticationRequest("emai-@'''.com", "qwe123"), propertyDataSource);
            Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
        }
    }

    @Nested
    class question {
        @Nested
        class create {
            @Test
            void valid() {
                QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                        new QuestionCreateRequest("How do I mock an autowired @Value field in Spring",
                                """


                                I'm using Spring 3.1.4.RELEASE and Mockito 1.9.5. In my Spring class I have:

                                @Value("#{myProps['default.url']}")
                                private String defaultUrl;

                                @Value("#{myProps['default.password']}")
                                private String defaultrPassword;

                                // ...

                                From my JUnit test, which I currently have set up like so:

                                @RunWith(SpringJUnit4ClassRunner.class)
                                @ContextConfiguration({ "classpath:test-context.xml" })
                                public class MyTest\s
                                {\s

                                I would like to mock a value for my "defaultUrl" field. Note that I don't want to mock values for the other fields 
                                — I'd like to keep those as they are, only the "defaultUrl" field. Also note that I have no explicit "setter" methods
                                 (e.g. setDefaultUrl) in my class and I don't want to create any just for the purposes of testing.

                                Given this, how can I mock a value for that one field?
                                """,
                                new String[]{"spring", "mockito", "autowired", "value-initialization"}), propertyDataSource);
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_title() {
                QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                        new QuestionCreateRequest("How do",
                                "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq",
                                new String[]{"spring", "mockito", "autowired", "value-initialization"}), propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                        new QuestionCreateRequest("How do I mock an autowired @Value field in Spring",
                                "how do",
                                new String[]{"spring", "mockito", "autowired", "value-initialization"}), propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_tags() {
                QuestionCreateRequestValidationWrapper validationWrapper = new QuestionCreateRequestValidationWrapper(
                        new QuestionCreateRequest(
                                "How do I mock an autowired @Value field in Spring",
                                """

                                        I'm using Spring 3.1.4.RELEASE and Mockito 1.9.5. In my Spring class I have:

                                        @Value("#{myProps['default.url']}")
                                        private String defaultUrl;""",
                                new String[]{null, "mockito", "autowired"}), propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));

                QuestionCreateRequestValidationWrapper validationWrapper1 = new QuestionCreateRequestValidationWrapper(
                        new QuestionCreateRequest(
                                "How do I mock an autowired @Value field in Spring",
                                """

                                        I'm using Spring 3.1.4.RELEASE and Mockito 1.9.5. In my Spring class I have:

                                        @Value("#{myProps['default.url']}")
                                        private String defaultUrl;""",
                                new String[]{"sp_!#@$", "q"}), propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper1));
            }
        }

        @Nested
        class edit {
            @Test
            void valid() {
                QuestionEditRequestValidationWrapper validationWrapper = new QuestionEditRequestValidationWrapper(
                        new QuestionEditRequest(
                                1L, """

                                I'm using Spring 3.1.4.RELEASE and Mockito 1.9.5. In my Spring class I have:

                                @Value("#{myProps['default.url']}")
                                private String defaultUrl;""",
                                new String[]{"spring", "mockito", "autowired", "value-initialization"}),
                        propertyDataSource);
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                QuestionEditRequestValidationWrapper validationWrapper = new QuestionEditRequestValidationWrapper(
                        new QuestionEditRequest(
                                -5L, "How do I mock an autowired @Value field in Spring",
                                new String[]{"spring", "mockito", "autowired", "value-initialization"}),
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
    }

    @Nested
    class answer {
        @Nested
        class create {
            @Test
            void valid() {
                AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                        new AnswerCreateRequest(
                                5L,
                                "You can use the magic of Spring's ReflectionTestUtils.setField" +
                                        "in order to avoid making any modifications whatsoever to your code."),
                                propertyDataSource);
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                        new AnswerCreateRequest(
                                -5L,
                                "You can use the magic of Spring's ReflectionTestUtils.setField" +
                                        "in order to avoid making any modifications whatsoever to your code."),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                AnswerCreateRequestValidationWrapper validationWrapper = new AnswerCreateRequestValidationWrapper(
                        new AnswerCreateRequest(
                                5L,
                                "idk"),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }
        }

        @Nested
        class edit {
            @Test
            void valid() {
                AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(
                        new AnswerEditRequest(1L,
                                "You can use the magic of Spring's ReflectionTestUtils.setField" +
                                        "in order to avoid making any modifications whatsoever to your code."),
                        propertyDataSource);
                Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_id() {
                AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(
                        new AnswerEditRequest(-5L,
                                "You can use the magic of Spring's ReflectionTestUtils.setField" +
                                        "in order to avoid making any modifications whatsoever to your code."),
                        propertyDataSource);
                Assertions.assertThrows(ValidationException.class, () -> validationChain.validateWithAdditionalValidator(validationWrapper));
            }

            @Test
            void invalid_text() {
                AnswerEditRequestValidationWrapper validationWrapper = new AnswerEditRequestValidationWrapper(
                        new AnswerEditRequest(1L,
                                "ahah lol! disvote."),
                        propertyDataSource);
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

    @Nested
    class comment {
        @Nested
        class question {
            @Nested
            class create {
                @Test
                void valid() {
                    CommentQuestionCreateRequestValidationWrapper validationWrapper = new CommentQuestionCreateRequestValidationWrapper(
                            new CommentQuestionCreateRequest(1L, "thank you! @username. :)"),
                            propertyDataSource);
                    Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }

                @Test
                void invalid_id() {
                    CommentQuestionCreateRequestValidationWrapper validationWrapper = new CommentQuestionCreateRequestValidationWrapper(
                            new CommentQuestionCreateRequest(-5L, "thank you! @username. :)"),
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
                            new CommentQuestionEditRequest(1L, "thank you! @username. :)"),
                            propertyDataSource);
                    Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }

                @Test
                void invalid_id() {
                    CommentQuestionEditRequestValidationWrapper validationWrapper = new CommentQuestionEditRequestValidationWrapper(
                            new CommentQuestionEditRequest(-5L, "thank you! @username. :)"),
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
                            new CommentAnswerCreateRequest(1L, "thank you! @username. :)"),
                            propertyDataSource);
                    Assertions.assertDoesNotThrow(() -> validationChain.validateWithAdditionalValidator(validationWrapper));
                }

                @Test
                void invalid_id() {
                    CommentAnswerCreateRequestValidationWrapper validationWrapper = new CommentAnswerCreateRequestValidationWrapper(
                            new CommentAnswerCreateRequest(-5L, "thank you! @username. :)"),
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
                            new CommentAnswerEditRequest(1L, "thank you! @username. :)"),
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
}
